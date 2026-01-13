package com.example.newsapp.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.data.local.Article
import com.example.newsapp.databinding.ItemArticleBinding

/**
 * Adapter sử dụng ListAdapter để tối ưu hóa việc cập nhật danh sách bằng DiffUtil.
 */
class ArticleAdapter(private val onItemClick: (Article) -> Unit) :
    ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ArticleViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.apply {
                // Hiển thị tiêu đề bài báo
                tvTitle.text = article.title

                // Xử lý trường hợp mô tả bị null để tránh để trống giao diện
                tvDescription.text = article.description ?: "Không có mô tả cho bài báo này."

                // Hiển thị ngày đăng (có thể cắt chuỗi để lấy phần ngày tháng)
                tvPublishedAt.text = article.publishedAt.substringBefore("T")

                // Sử dụng Glide để tải ảnh bài báo
                Glide.with(ivArticleImage.context)
                    .load(article.urlToImage)
                    .placeholder(R.drawable.ic_launcher_background) // Ảnh hiển thị khi đang tải
                    .error(android.R.drawable.ic_menu_report_image) // Ảnh hiển thị khi lỗi tải
                    .centerCrop()
                    .into(ivArticleImage)

                // Xử lý sự kiện click vào mục bài báo
                root.setOnClickListener { onItemClick(article) }
            }
        }
    }

    /**
     * Lớp hỗ trợ so sánh dữ liệu cũ và mới để tối ưu việc cập nhật UI.
     */
    class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            // So sánh dựa trên URL vì đây là khóa chính
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            // So sánh toàn bộ nội dung đối tượng
            return oldItem == newItem
        }
    }
}
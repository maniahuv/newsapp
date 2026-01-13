package com.example.newsapp.ui.list

import android.graphics.Color
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
 * Adapter sử dụng ListAdapter để tối ưu hóa việc cập nhật danh sách.
 */
class ArticleAdapter(
    private val onItemClick: (Article) -> Unit,
    private val onFavoriteClick: (Article) -> Unit
) : ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallback()) {

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
                // 1. Hiển thị thông tin văn bản (Xử lý Null-safe)
                tvTitle.text = article.title ?: "Không có tiêu đề"

                // Kiểm tra description null hoặc trống
                tvDescription.text = if (article.description.isNullOrBlank()) {
                    "Không có mô tả cho bài báo này."
                } else {
                    article.description
                }

                // Cắt chuỗi ngày tháng an toàn với ?.
                tvPublishedAt.text = article.publishedAt?.substringBefore("T") ?: "Không rõ ngày"

                // 2. Cập nhật trạng thái nút Yêu thích
                // Sử dụng icon trái tim đã tạo để đồng bộ với DetailFragment
                val favoriteIcon = if (article.isFavorite) {
                    R.drawable.ic_favorite_filled
                } else {
                    R.drawable.ic_favorite_border
                }
                ivFavorite.setImageResource(favoriteIcon)

                // Thay đổi màu sắc (Tint) để icon trông nổi bật
                val tintColor = if (article.isFavorite) {
                    Color.parseColor("#E91E63") // Màu hồng đỏ
                } else {
                    Color.parseColor("#9E9E9E") // Màu xám
                }
                ivFavorite.setColorFilter(tintColor)

                // 3. Tải ảnh bằng Glide
                Glide.with(ivArticleImage.context)
                    .load(article.urlToImage)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(android.R.drawable.ic_menu_report_image)
                    .centerCrop()
                    .into(ivArticleImage)

                // 4. Xử lý các sự kiện click
                root.setOnClickListener { onItemClick(article) }

                ivFavorite.setOnClickListener {
                    onFavoriteClick(article)
                }
            }
        }
    }

    class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
}
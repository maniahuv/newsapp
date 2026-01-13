package com.example.newsapp.ui.list

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.data.local.Article
import com.example.newsapp.databinding.ItemArticleBinding

/**
 * ViewHolder riêng biệt để quản lý việc hiển thị dữ liệu của một bài báo.
 */
class ArticleViewHolder(
    private val binding: ItemArticleBinding,
    private val onItemClick: (Article) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(article: Article) {
        binding.apply {
            // Hiển thị các thông tin văn bản
            tvTitle.text = article.title
            tvDescription.text = article.description ?: "Không có mô tả"

            // Định dạng lại chuỗi ngày tháng (cắt lấy phần yyyy-MM-dd)
            tvPublishedAt.text = article.publishedAt.substringBefore("T")

            // Sử dụng Glide để tải hình ảnh bài báo
            Glide.with(ivArticleImage.context)
                .load(article.urlToImage)
                .placeholder(R.drawable.ic_launcher_background) // Ảnh chờ khi đang tải
                .error(android.R.drawable.ic_menu_report_image) // Ảnh hiển thị khi lỗi
                .centerCrop()
                .into(ivArticleImage)

            // Thiết lập sự kiện click cho toàn bộ item
            root.setOnClickListener { onItemClick(article) }
        }
    }
}
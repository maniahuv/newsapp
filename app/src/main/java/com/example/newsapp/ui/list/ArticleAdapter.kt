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
 * Khi biến isFavorite thay đổi, DiffUtil sẽ phát hiện và chỉ vẽ lại item đó.
 */
class ArticleAdapter(
    private val onItemClick: (Article) -> Unit,
    private val onFavoriteClick: (Article) -> Unit // Callback xử lý lưu/xóa yêu thích
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
                // 1. Hiển thị thông tin văn bản
                tvTitle.text = article.title
                tvDescription.text = article.description.ifBlank { "Không có mô tả cho bài báo này." }
                tvPublishedAt.text = article.publishedAt.substringBefore("T") // Cắt chuỗi lấy yyyy-MM-dd

                // 2. Cập nhật trạng thái và màu sắc nút Yêu thích
                // Sử dụng icon sao đặc nếu đã lưu, sao rỗng nếu chưa
                val favoriteIcon = if (article.isFavorite) {
                    android.R.drawable.btn_star_big_on
                } else {
                    android.R.drawable.btn_star_big_off
                }
                ivFavorite.setImageResource(favoriteIcon)

                // Sửa lỗi "luôn màu tím": Thay đổi màu sắc (Tint) bằng code
                // Nếu đã lưu thì hiện màu Vàng Gold, nếu chưa thì hiện màu Xám
                val tintColor = if (article.isFavorite) {
                    Color.parseColor("#FFD700") // Vàng Gold
                } else {
                    Color.parseColor("#9E9E9E") // Xám
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
            // Quan trọng: Phải so sánh toàn bộ object để DiffUtil thấy sự thay đổi của isFavorite
            return oldItem == newItem
        }
    }
}
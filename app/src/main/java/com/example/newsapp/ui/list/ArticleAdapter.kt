package com.example.newsapp.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.data.local.Article
import com.example.newsapp.databinding.ItemArticleBinding

class ArticleAdapter(private val onItemClick: (Article) -> Unit) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private var articles = listOf<Article>()

    fun setArticles(newArticles: List<Article>) {
        this.articles = newArticles
        notifyDataSetChanged() // Bạn có thể dùng DiffUtil sau để mượt hơn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount() = articles.size

    inner class ArticleViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.tvTitle.text = article.title
            binding.tvDescription.text = article.description
            binding.tvPublishedAt.text = article.publishedAt

            // Dùng Glide để load ảnh từ URL
            Glide.with(binding.ivArticleImage.context)
                .load(article.urlToImage)
                .placeholder(R.drawable.ic_launcher_background) // Ảnh tạm khi đang load
                .error(android.R.drawable.ic_menu_report_image) // Ảnh lỗi khi offline
                .into(binding.ivArticleImage)

            binding.root.setOnClickListener { onItemClick(article) }
        }
    }
}
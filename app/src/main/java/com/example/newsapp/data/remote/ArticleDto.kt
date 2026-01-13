package com.example.newsapp.data.remote

data class ArticleDto(
    val title: String,
    val author: String?,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?
)

// Có thể để hàm convert ở đây luôn cho tiện
fun ArticleDto.toEntity(): com.example.newsapp.data.local.Article {
    return com.example.newsapp.data.local.Article(
        url = this.url,
        title = this.title ?: "",
        author = this.author,
        description = this.description,
        urlToImage = this.urlToImage,
        publishedAt = this.publishedAt,
        content = this.content
    )
}
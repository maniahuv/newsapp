package com.example.newsapp.data.remote

import com.example.newsapp.data.local.Article

// File: data/remote/NewsResponse.kt
data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<ArticleDto>
)



package com.example.newsapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey val url: String, // Dùng URL làm khóa chính để không bị trùng bài báo
    val title: String,
    val author: String?,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
    var isFavorite: Boolean = false // Thêm trường này để làm chức năng "Yêu thích"
)
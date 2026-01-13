package com.example.newsapp.data.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

// Sử dụng @Parcelize để có thể truyền object Article giữa các Fragment/Activity
@Parcelize
@Entity(tableName = "articles")
data class Article(
    @PrimaryKey
    val url: String, // Dùng URL làm khóa chính để tránh trùng lặp bài báo

    val title: String,
    val author: String,
    val description: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String,

    // Trường này dùng để xử lý logic lưu bài báo yêu thích, không bị ghi đè khi làm mới API
    val isFavorite: Boolean = false
) : Parcelable
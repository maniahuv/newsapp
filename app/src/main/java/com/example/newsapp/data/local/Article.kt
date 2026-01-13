package com.example.newsapp.data.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Sử dụng @Parcelize để có thể truyền object Article giữa các Fragment/Activity thông qua Safe Args.
 * Lớp này vừa là Data Class thông thường, vừa là Table trong Room Database.
 */
@Parcelize
@Entity(tableName = "articles")
data class Article(
    @PrimaryKey
    val url: String, // Dùng URL làm khóa chính (Primary Key) để tránh trùng lặp bài báo

    val title: String?,
    val author: String?,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?, // Đây thường là đoạn trích ngắn (+200 chars) từ API

    // Trường dùng để xử lý logic lưu bài báo yêu thích
    var isFavorite: Boolean = false,

    /**
     * TRƯỜNG MỚI: Lưu toàn bộ nội dung bài báo sau khi cào dữ liệu bằng Jsoup.
     * Thuộc tính này cho phép đọc Offline hoàn toàn mà không cần Internet.
     */
    var savedFullContent: String? = null

) : Parcelable
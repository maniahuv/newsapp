package com.example.newsapp.data.remote

import com.example.newsapp.data.local.Article
import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) đại diện cho cấu trúc JSON từ NewsAPI.
 * Tất cả các trường nên để kiểu Nullable (?) để tránh Crash khi API thiếu dữ liệu.
 */
data class ArticleDto(
    @SerializedName("title")
    val title: String?,

    @SerializedName("author")
    val author: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("url")
    val url: String?,

    @SerializedName("urlToImage")
    val urlToImage: String?,

    @SerializedName("publishedAt")
    val publishedAt: String?,

    @SerializedName("content")
    val content: String?
)

/**
 * Hàm mở rộng để chuyển đổi từ DTO (dữ liệu mạng) sang Entity (dữ liệu local).
 * Việc sử dụng mapNotNull trong Repository kết hợp với hàm này sẽ giúp lọc bỏ các bài báo lỗi.
 */
fun ArticleDto.toEntity(): Article? {
    // Kiểm tra các trường bắt buộc. Nếu không có URL hoặc Tiêu đề, bài báo này không hợp lệ.
    if (url.isNullOrBlank() || title.isNullOrBlank()) return null

    return Article(
        url = url,
        title = title,
        author = author ?: "Unknown Author", // Gán giá trị mặc định nếu null
        description = description ?: "No description available",
        urlToImage = urlToImage?:"", // Glide/Coil sẽ tự xử lý nếu URL ảnh null
        publishedAt = publishedAt ?: "",
        content = content ?: "",
        isFavorite = false // Khi lấy từ API về, mặc định luôn là chưa yêu thích
    )
}


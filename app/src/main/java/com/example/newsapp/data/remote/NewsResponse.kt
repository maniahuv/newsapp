package com.example.newsapp.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Lớp đại diện cho phản hồi cấp cao nhất từ NewsAPI.
 * Khi API trả về kết quả thành công, nó sẽ khớp với cấu trúc này.
 */
data class NewsResponse(
    @SerializedName("status")
    val status: String?, // "ok" hoặc "error"

    @SerializedName("totalResults")
    val totalResults: Int?, // Tổng số bài báo tìm thấy

    @SerializedName("articles")
    val articles: List<ArticleDto>? // Danh sách các bài báo chi tiết
)
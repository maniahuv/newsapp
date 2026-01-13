package com.example.newsapp.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    /**
     * Lấy danh sách tin tức hàng đầu theo quốc gia và danh mục.
     * @param country Mã quốc gia (ví dụ: "us").
     * @param category Danh mục tin tức (business, entertainment, general, health, science, sports, technology).
     * @param apiKey Khóa API.
     */
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String,
        @Query("category") category: String, // ĐÃ BỔ SUNG: Để lọc theo Tab danh mục
        @Query("apiKey") apiKey: String
    ): Response<NewsResponse>

    /**
     * Tìm kiếm bài báo theo từ khóa.
     * CẬP NHẬT: Thêm searchIn và sortBy để lọc kết quả chính xác hơn.
     * @param query Từ khóa tìm kiếm.
     * @param apiKey Khóa API.
     * @param searchIn Nơi tìm kiếm từ khóa (mặc định: title,description để tránh kết quả rác).
     * @param sortBy Cách sắp xếp (mặc định: relevancy để bài liên quan nhất hiện lên đầu).
     * @param language Ngôn ngữ của bài báo (ví dụ: "en" hoặc "vi").
     */
    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String,
        @Query("searchIn") searchIn: String = "title,description",
        @Query("sortBy") sortBy: String = "relevancy",
        @Query("language") language: String? = null
    ): Response<NewsResponse>
}
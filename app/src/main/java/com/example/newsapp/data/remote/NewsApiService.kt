package com.example.newsapp.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    /**
     * Lấy danh sách tin tức hàng đầu.
     * * @param country Mã quốc gia (ví dụ: "us", "id"). Lưu ý: "vi" có thể không trả về dữ liệu
     * trên một số gói NewsAPI miễn phí.
     * @param category Danh mục tin tức (business, entertainment, health, science, sports, technology).
     * @param apiKey Khóa API lấy từ RetrofitClient.API_KEY.
     */
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String
    ): Response<NewsResponse>

    /**
     * Tìm kiếm bài báo theo từ khóa.
     */
    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String
    ): Response<NewsResponse>
}
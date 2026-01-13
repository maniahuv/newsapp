package com.example.newsapp.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "vi",
        @Query("apiKey") apiKey: String = "YOUR_API_KEY_HERE"
    ): Response<NewsResponse> // NewsResponse là class bọc kết quả trả về từ API
}
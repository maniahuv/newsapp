package com.example.newsapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// File: data/remote/RetrofitClient.kt
object RetrofitClient {
    private const val BASE_URL = "https://newsapi.org/"
    const val API_KEY = "9cd24df32c8542208fe943a19d70c70c"
    val instance: NewsApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(NewsApiService::class.java)
    }
}
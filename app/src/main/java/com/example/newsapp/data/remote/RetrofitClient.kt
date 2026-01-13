package com.example.newsapp.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://newsapi.org/"

    // Lưu ý: Bảo mật API Key là quan trọng, trong thực tế nên để ở local.properties
    const val API_KEY = "9cd24df32c8542208fe943a19d70c70c"

    // Tạo bộ ghi log để kiểm tra dữ liệu mạng trong Logcat (Tag: OkHttp)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cấu hình OkHttpClient với thời gian chờ và logging
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Đổi tên từ 'instance' thành 'apiService' để khớp với lệnh gọi trong MainActivity:
     * NewsRepository(articleDao, RetrofitClient.apiService)
     */
    val apiService: NewsApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Gán client đã cấu hình vào Retrofit
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(NewsApiService::class.java)
    }
}
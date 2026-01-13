package com.example.newsapp

import android.app.Application
import com.example.newsapp.data.local.NewsDatabase
import com.example.newsapp.data.remote.RetrofitClient
import com.example.newsapp.data.repository.NewsRepository

/**
 * Lớp Application tùy chỉnh cho ứng dụng.
 * Quản lý các thành phần Singleton để dùng chung cho toàn bộ App.
 */
class NewsApplication : Application() {

    // 1. Đổi AppDatabase thành NewsDatabase cho khớp với file đã sửa
    private val database by lazy {
        NewsDatabase.getDatabase(this)
    }

    // 2. Đổi database.articleDao() thành database.getArticleDao()
    // 3. Đổi RetrofitClient.instance thành RetrofitClient.apiService
    val repository by lazy {
        NewsRepository(database.getArticleDao(), RetrofitClient.apiService)
    }

    override fun onCreate() {
        super.onCreate()
    }
}
package com.example.newsapp

import android.app.Application
import com.example.newsapp.data.local.AppDatabase
import com.example.newsapp.data.repository.NewsRepository


// File: NewsApplication.kt
class NewsApplication : Application() {
    // Khởi tạo Database chậm (lazy) khi cần dùng
    private val database by lazy { AppDatabase.getDatabase(this) }

    // Khởi tạo Repository
    val repository by lazy { NewsRepository(database.articleDao()) }
}
package com.example.newsapp

import android.app.Application
import com.example.newsapp.data.local.AppDatabase
import com.example.newsapp.data.repository.NewsRepository

/**
 * Lớp Application tùy chỉnh cho ứng dụng.
 * Đây là nơi quản lý các thành phần dùng chung duy nhất (Singleton) như Database và Repository.
 */
class NewsApplication : Application() {

    // Sử dụng 'by lazy' để Database chỉ được tạo khi thực sự được gọi tới lần đầu tiên.
    // Việc này giúp ứng dụng khởi động nhanh hơn.
    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    // Repository là nguồn dữ liệu duy nhất cho toàn bộ ứng dụng.
    // Nó được khởi tạo bằng cách truyền vào ArticleDao từ Database.
    val repository by lazy {
        NewsRepository(database.articleDao())
    }

    override fun onCreate() {
        super.onCreate()
        // Bạn có thể khởi tạo các thư viện khác ở đây nếu cần (ví dụ: Timber để Log, v.v.)
    }
}
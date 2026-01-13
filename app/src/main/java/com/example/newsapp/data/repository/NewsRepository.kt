package com.example.newsapp.data.repository

import androidx.lifecycle.LiveData
import com.example.newsapp.data.local.Article
import com.example.newsapp.data.local.ArticleDao
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.data.remote.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// File: data/repository/NewsRepository.kt
class NewsRepository(private val articleDao: ArticleDao) {

    // Đây là nguồn dữ liệu duy nhất mà UI sẽ quan sát
    val allArticles: LiveData<List<Article>> = articleDao.getAllArticles()

    suspend fun refreshArticles(apiService: NewsApiService) {
        // Chạy trong Dispatchers.IO để không gây lag giao diện
        withContext(Dispatchers.IO) {
            try {
                // 1. Gọi API lấy tin mới
                val response = apiService.getTopHeadlines()

                if (response.isSuccessful) {
                    val articlesApi = response.body()?.articles ?: emptyList()

                    // 2. Chuyển đổi sang dạng Entity của Room
                    val entities = articlesApi.map { it.toEntity() }

                    // 3. Lưu vào Database (Room sẽ tự động kích hoạt LiveData để cập nhật UI)
                    articleDao.insertArticles(entities)
                }
            } catch (e: Exception) {
                // Nếu offline hoặc lỗi API, code sẽ rơi vào đây.
                // Chúng ta không cần làm gì thêm vì UI vẫn đang 'quan sát' Room,
                // nơi vẫn còn dữ liệu của lần tải thành công trước đó.
                e.printStackTrace()
            }
        }
    }
}
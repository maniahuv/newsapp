package com.example.newsapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.newsapp.data.local.Article
import com.example.newsapp.data.local.ArticleDao
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.data.remote.RetrofitClient
import com.example.newsapp.data.remote.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsRepository(private val articleDao: ArticleDao) {

    // Đây là nguồn dữ liệu duy nhất mà UI sẽ quan sát
    val allArticles: LiveData<List<Article>> = articleDao.getAllArticles()

    suspend fun refreshArticles(apiService: NewsApiService) {
        withContext(Dispatchers.IO) {
            try {
                // 1. Gọi API lấy tin mới (Truyền API_KEY thực tế từ RetrofitClient)
                val response = apiService.getTopHeadlines(apiKey = RetrofitClient.API_KEY)

                if (response.isSuccessful) {
                    val articlesApi = response.body()?.articles ?: emptyList()

                    // 2. Chuyển đổi sang dạng Entity của Room
                    val entities = articlesApi.map { it.toEntity() }

                    // 3. Cập nhật Database (Sử dụng insertAll và deleteAll đã sửa ở DAO)
                    articleDao.deleteAll()
                    articleDao.insertAll(entities)
                } else {
                    // THÊM NHÁNH ELSE NÀY ĐỂ SỬA LỖI "if must have both main and else branches"
                    Log.e("NewsRepository", "Lỗi API: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                // Xử lý lỗi kết nối
                Log.e("NewsRepository", "Lỗi kết nối: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
package com.example.newsapp.data.repository

import androidx.lifecycle.LiveData
import com.example.newsapp.data.local.Article
import com.example.newsapp.data.local.ArticleDao
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.data.remote.RetrofitClient // Import để dùng API_KEY
import com.example.newsapp.data.remote.toEntity       // Import hàm mở rộng toEntity()
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NewsRepository(private val articleDao: ArticleDao) {

    // Lấy toàn bộ bài báo từ database (Single Source of Truth)
    val allArticles: LiveData<List<Article>> = articleDao.getAllArticles()

    // Lấy danh sách bài báo yêu thích
    val favoriteArticles: LiveData<List<Article>> = articleDao.getFavoriteArticles()

    /**
     * Tải tin tức mới từ API và lưu vào database.
     */
    suspend fun refreshArticles(apiService: NewsApiService) {
        withContext(Dispatchers.IO) {
            try {
                // 1. Sử dụng API_KEY tập trung từ RetrofitClient thay vì hardcode
                val response = apiService.getTopHeadlines("us", RetrofitClient.API_KEY)

                if (response.isSuccessful) {
                    response.body()?.articles?.let { remoteArticles ->
                        // 2. Sử dụng mapNotNull kết hợp toEntity() để lọc bỏ các bài báo
                        // có URL bị null hoặc dữ liệu lỗi (tránh crash Room Primary Key)
                        val localArticles = remoteArticles.mapNotNull { it.toEntity() }

                        // 3. Sử dụng deleteAllNonFavorites() thay vì deleteAll()
                        // để giữ lại các bài báo người dùng đã nhấn "Yêu thích"
                        articleDao.deleteAllNonFavorites()

                        // 4. Chèn dữ liệu mới đã được chuẩn hóa
                        articleDao.insertAll(localArticles)
                    }
                }
            } catch (e: Exception) {
                // Có thể log lỗi ở đây hoặc throw để UI xử lý
                throw e
            }
        }
    }

    /**
     * Đảo ngược trạng thái yêu thích và cập nhật vào Database.
     */
    suspend fun toggleFavorite(article: Article) {
        // Đảo trạng thái Boolean một cách ngắn gọn
        article.isFavorite = !article.isFavorite

        // Cập nhật xuống database
        articleDao.updateArticle(article)
    }
}
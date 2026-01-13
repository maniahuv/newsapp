package com.example.newsapp.data.repository

import androidx.lifecycle.LiveData
import com.example.newsapp.data.local.Article
import com.example.newsapp.data.local.ArticleDao
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.data.remote.RetrofitClient
import com.example.newsapp.data.remote.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository quản lý việc điều phối dữ liệu giữa Local Database (Room) và Remote API (Retrofit).
 * Đảm bảo ứng dụng có thể hoạt động Offline bằng cách luôn hiển thị dữ liệu từ Room.
 */
class NewsRepository(
    private val articleDao: ArticleDao,
    private val apiService: NewsApiService
) {

    // Danh sách toàn bộ bài báo từ DB, hiển thị ở trang chủ
    val allArticles: LiveData<List<Article>> = articleDao.getAllArticles()

    // Danh sách các bài báo người dùng đã nhấn Yêu thích
    val favoriteArticles: LiveData<List<Article>> = articleDao.getFavoriteArticles()

    /**
     * Tải tin tức mới từ API và cập nhật vào Database.
     */
    suspend fun refreshArticles() {
        withContext(Dispatchers.IO) {
            try {
                // Gọi API lấy tin tức mới nhất
                val response = apiService.getTopHeadlines("us", RetrofitClient.API_KEY)

                if (response.isSuccessful) {
                    response.body()?.articles?.let { remoteArticles ->
                        // Chuyển đổi từ dữ liệu API (DTO) sang thực thể Database (Entity)
                        val localArticles = remoteArticles.mapNotNull { it.toEntity() }

                        if (localArticles.isNotEmpty()) {
                            // CHIẾN LƯỢC CẬP NHẬT:
                            // 1. Xóa các bài báo cũ để tránh tràn bộ nhớ (trừ những bài được Yêu thích)
                            articleDao.deleteAllNonFavorites()

                            // 2. Chèn danh sách mới vào.
                            // Lưu ý: Trong ArticleDao, hãy dùng OnConflictStrategy.IGNORE
                            // để tránh ghi đè làm mất trạng thái 'isFavorite' của tin cũ đang xuất hiện lại.
                            articleDao.insertAll(localArticles)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Đảo ngược trạng thái Yêu thích (Favorite/Unfavorite).
     */
    suspend fun toggleFavorite(article: Article) {
        withContext(Dispatchers.IO) {
            val updatedArticle = article.copy(isFavorite = !article.isFavorite)
            articleDao.updateArticle(updatedArticle)
        }
    }

    /**
     * Tìm kiếm bài báo (Kết quả tìm kiếm cũng được lưu vào DB để xem offline)
     */
    suspend fun searchNews(query: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchNews(query, RetrofitClient.API_KEY)
                if (response.isSuccessful) {
                    response.body()?.articles?.let { remoteArticles ->
                        val localArticles = remoteArticles.mapNotNull { it.toEntity() }
                        articleDao.insertAll(localArticles)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
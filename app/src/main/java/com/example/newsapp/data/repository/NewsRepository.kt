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
    private val apiService: NewsApiService // Thêm vào constructor để quản lý tập trung
) {

    // Danh sách toàn bộ bài báo, LiveData sẽ tự động cập nhật khi Database thay đổi
    val allArticles: LiveData<List<Article>> = articleDao.getAllArticles()

    // Danh sách bài báo đã được đánh dấu yêu thích
    val favoriteArticles: LiveData<List<Article>> = articleDao.getFavoriteArticles()

    /**
     * Tải tin tức mới từ API và cập nhật vào Database.
     * Sử dụng Dispatchers.IO để không gây treo giao diện (UI Thread).
     */
    suspend fun refreshArticles() {
        withContext(Dispatchers.IO) {
            try {
                // Lấy tin tức từ API sử dụng API_KEY tập trung
                val response = apiService.getTopHeadlines("us", RetrofitClient.API_KEY)

                if (response.isSuccessful) {
                    response.body()?.articles?.let { remoteArticles ->
                        // Chuyển đổi DTO sang Entity và lọc bỏ bài báo không hợp lệ (URL null/empty)
                        // Việc này cực kỳ quan trọng vì URL là Khóa chính (Primary Key) trong Room
                        val localArticles = remoteArticles.mapNotNull { it.toEntity() }

                        if (localArticles.isNotEmpty()) {
                            // 1. Xóa các bài báo cũ NHƯNG giữ lại bài báo Yêu thích
                            articleDao.deleteAllNonFavorites()

                            // 2. Lưu danh sách mới vào Database
                            articleDao.insertAll(localArticles)
                        }
                    }
                }
            } catch (e: Exception) {
                // In lỗi ra console để debug, LiveData vẫn giữ dữ liệu cũ để xem Offline
                e.printStackTrace()
            }
        }
    }

    /**
     * Cập nhật trạng thái Yêu thích của bài báo.
     * Sử dụng .copy() để đảm bảo tính bất biến (Immutability) của Data Class.
     */
    suspend fun toggleFavorite(article: Article) {
        withContext(Dispatchers.IO) {
            val updatedArticle = article.copy(isFavorite = !article.isFavorite)
            articleDao.updateArticle(updatedArticle)
        }
    }

    /**
     * Tìm kiếm bài báo từ API (Tính năng mở rộng)
     */
    suspend fun searchNews(query: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchNews(query, RetrofitClient.API_KEY)
                if (response.isSuccessful) {
                    response.body()?.articles?.let { remoteArticles ->
                        val localArticles = remoteArticles.mapNotNull { it.toEntity() }
                        // Chèn thêm vào DB, nếu trùng URL sẽ tự động ghi đè nhờ OnConflictStrategy.REPLACE
                        articleDao.insertAll(localArticles)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
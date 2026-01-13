package com.example.newsapp.data.repository

import androidx.lifecycle.LiveData
import com.example.newsapp.data.local.Article
import com.example.newsapp.data.local.ArticleDao
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.data.remote.RetrofitClient
import com.example.newsapp.data.remote.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup // Import thư viện Jsoup

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
                val response = apiService.getTopHeadlines("us", RetrofitClient.API_KEY)
                if (response.isSuccessful) {
                    response.body()?.articles?.let { remoteArticles ->
                        val localArticles = remoteArticles.mapNotNull { it.toEntity() }
                        if (localArticles.isNotEmpty()) {
                            articleDao.deleteAllNonFavorites()
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
     * Đảo ngược trạng thái Yêu thích và tải nội dung Offline nếu cần.
     */
    suspend fun toggleFavorite(article: Article) {
        withContext(Dispatchers.IO) {
            val isBecomingFavorite = !article.isFavorite

            val updatedArticle = if (isBecomingFavorite) {
                // Nếu người dùng nhấn yêu thích -> Tải nội dung bài báo đầy đủ
                val fullContent = downloadFullContent(article.url)
                article.copy(
                    isFavorite = true,
                    savedFullContent = fullContent // Lưu văn bản đã cào được
                )
            } else {
                // Nếu người dùng bỏ yêu thích -> Xóa nội dung offline để giải phóng bộ nhớ
                article.copy(
                    isFavorite = false,
                    savedFullContent = null
                )
            }

            articleDao.updateArticle(updatedArticle)
        }
    }

    /**
     * Hàm sử dụng Jsoup để tải HTML và trích xuất nội dung văn bản chính.
     */
    private suspend fun downloadFullContent(url: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Kết nối tới URL và tải tài liệu HTML (Timeout sau 10 giây)
                val doc = Jsoup.connect(url).timeout(10000).get()

                // Lấy tất cả các thẻ <p> (đoạn văn) trong bài báo
                val paragraphs = doc.select("p")
                val contentBuilder = StringBuilder()

                for (p in paragraphs) {
                    val text = p.text().trim()
                    if (text.isNotEmpty()) {
                        contentBuilder.append(text).append("\n\n")
                    }
                }

                val result = contentBuilder.toString()
                if (result.isBlank()) "Không tìm thấy nội dung văn bản phù hợp." else result
            } catch (e: Exception) {
                e.printStackTrace()
                "Lỗi khi tải nội dung bài báo: ${e.message}"
            }
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
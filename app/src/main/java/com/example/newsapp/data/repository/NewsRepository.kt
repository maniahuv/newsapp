package com.example.newsapp.data.repository

import androidx.lifecycle.LiveData
import com.example.newsapp.data.local.Article
import com.example.newsapp.data.local.ArticleDao
import com.example.newsapp.data.remote.NewsApiService
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
                val response = apiService.getTopHeadlines("us", "9cd24df32c8542208fe943a19d70c70c")
                if (response.isSuccessful) {
                    response.body()?.articles?.let { remoteArticles ->
                        // Chuyển đổi dữ liệu từ API sang Entity của Room
                        val localArticles = remoteArticles.map { remote ->
                            Article(
                                url = remote.url ?: "",                 // ❗ BẮT BUỘC
                                title = remote.title ?: "No Title",
                                author = remote.author ?: "",
                                description = remote.description ?: "",
                                urlToImage = remote.urlToImage ?: "",
                                publishedAt = remote.publishedAt ?: "",
                                content = remote.content ?: ""
                            )
                        }
                        // Xóa tin cũ và chèn tin mới để đảm bảo dữ liệu luôn mới nhất
                        articleDao.deleteAll()
                        articleDao.insertArticles(localArticles)
                    }
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Hàm đảo ngược trạng thái yêu thích.
     * ĐÂY LÀ NƠI DỄ GÂY LỖI 'if must have both branches'
     */
    suspend fun toggleFavorite(article: Article) {
        // CÁCH VIẾT SAI GÂY LỖI: article.isFavorite = if (article.isFavorite) false

        // CÁCH VIẾT ĐÚNG 1: Sử dụng ! (Nên dùng cách này cho Boolean)
        article.isFavorite = !article.isFavorite

        // CÁCH VIẾT ĐÚNG 2 (Nếu dùng if): Phải có đủ ELSE
        // article.isFavorite = if (article.isFavorite) false else true

        articleDao.updateArticle(article)
    }
}
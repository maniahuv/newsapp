package com.example.newsapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.local.Article
import com.example.newsapp.data.remote.RetrofitClient
import com.example.newsapp.data.repository.NewsRepository
import kotlinx.coroutines.launch

/**
 * ViewModel quản lý dữ liệu cho màn hình danh sách tin tức.
 */
class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    // 1. Quan sát danh sách bài báo từ Repository (Single Source of Truth)
    val allArticles: LiveData<List<Article>> = repository.allArticles

    // 2. Quan sát danh sách bài báo yêu thích
    val favoriteArticles: LiveData<List<Article>> = repository.favoriteArticles

    // 3. Trạng thái Loading để hiển thị ProgressBar trên UI
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // 4. Thông báo lỗi để hiển thị cho người dùng
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        // Tự động tải tin tức mới ngay khi khởi tạo ViewModel
        refreshNews()
    }

    /**
     * Làm mới danh sách tin tức từ Server.
     */
    fun refreshNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Gọi Repository để lấy dữ liệu mới từ API và cập nhật Database
                repository.refreshArticles(RetrofitClient.instance)
            } catch (e: Exception) {
                // Xử lý các lỗi kết nối hoặc phân tích dữ liệu
                _errorMessage.value = "Không thể cập nhật tin tức: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Chức năng thay đổi trạng thái "Yêu thích" của bài báo.
     */
    fun toggleFavorite(article: Article) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(article)
            } catch (e: Exception) {
                _errorMessage.value = "Không thể cập nhật trạng thái yêu thích"
            }
        }
    }

    /**
     * (Tùy chọn) Chức năng tìm kiếm tin tức theo từ khóa.
     */
    fun searchNews(query: String) {
        if (query.isBlank()) {
            refreshNews()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Logic tìm kiếm có thể được bổ sung thêm vào Repository
                // Ví dụ: repository.searchArticles(RetrofitClient.instance, query)
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi tìm kiếm: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Xóa thông báo lỗi sau khi đã hiển thị trên UI.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
package com.example.newsapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.local.Article
import com.example.newsapp.data.repository.NewsRepository
import kotlinx.coroutines.launch

/**
 * ViewModel quản lý logic và trạng thái dữ liệu cho các màn hình tin tức.
 * Cung cấp dữ liệu từ Room Database dưới dạng LiveData để UI tự động cập nhật.
 */
class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    // 1. Luồng dữ liệu "Sống": Quan sát trực tiếp từ Database thông qua Repository.
    // Màn hình danh sách chính sẽ quan sát biến này.
    val allArticles: LiveData<List<Article>> = repository.allArticles

    // Màn hình "Yêu thích" sẽ quan sát biến này.
    val favoriteArticles: LiveData<List<Article>> = repository.favoriteArticles

    // 2. Quản lý trạng thái UI (Loading, Error).
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        // Tự động tải tin mới nhất từ API ngay khi ứng dụng khởi chạy.
        refreshNews()
    }

    /**
     * Đồng bộ tin tức từ API về Database.
     * UI sẽ tự động cập nhật khi Database có dữ liệu mới nhờ LiveData.
     */
    fun refreshNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.refreshArticles()
            } catch (e: Exception) {
                // Nếu lỗi (ví dụ mất mạng), app vẫn hiển thị dữ liệu cũ trong Room.
                _errorMessage.value = "Lỗi cập nhật: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Xử lý logic khi người dùng nhấn nút Yêu thích bài báo.
     */
    fun toggleFavorite(article: Article) {
        viewModelScope.launch {
            try {
                repository.toggleFavorite(article)
            } catch (e: Exception) {
                _errorMessage.value = "Không thể cập nhật yêu thích"
            }
        }
    }

    /**
     * Tìm kiếm tin tức theo từ khóa.
     * Kết quả tìm kiếm sẽ được lưu vào Database và hiển thị qua allArticles.
     */
    fun searchNews(query: String) {
        if (query.isBlank()) {
            refreshNews()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.searchNews(query)
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi tìm kiếm: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Xóa thông báo lỗi sau khi đã hiển thị trên UI (ví dụ qua Toast hoặc Snackbar).
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
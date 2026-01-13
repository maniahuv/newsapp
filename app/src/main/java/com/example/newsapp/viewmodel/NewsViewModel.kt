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
    val allArticles: LiveData<List<Article>> = repository.allArticles
    val favoriteArticles: LiveData<List<Article>> = repository.favoriteArticles

    // 2. Quản lý trạng thái UI (Loading, Error).
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        refreshNews()
    }

    /**
     * Đồng bộ tin tức từ API về Database.
     */
    fun refreshNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.refreshArticles()
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi cập nhật: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Xử lý logic khi người dùng nhấn nút Yêu thích bài báo.
     * CẬP NHẬT: Thêm loading vì quá trình này bao gồm việc cào dữ liệu HTML (Offline Mode).
     */
    fun toggleFavorite(article: Article) {
        viewModelScope.launch {
            _isLoading.value = true // Hiện loading trong lúc Jsoup đang cào bài báo
            try {
                repository.toggleFavorite(article)
            } catch (e: Exception) {
                _errorMessage.value = "Không thể lưu bài báo để đọc offline"
            } finally {
                _isLoading.value = false // Tắt loading khi đã lưu xong vào Room
            }
        }
    }

    /**
     * Tìm kiếm tin tức theo từ khóa.
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

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
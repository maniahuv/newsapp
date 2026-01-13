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

    // 1. Quan sát danh sách bài báo từ Repository (Single Source of Truth từ Room)
    val allArticles: LiveData<List<Article>> = repository.allArticles

    // 2. Trạng thái Loading để hiển thị ProgressBar trên UI
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // 3. Thông báo lỗi để hiển thị Toast hoặc Snackbar khi có sự cố (mất mạng, lỗi API)
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    init {
        // Tự động tải tin tức mới ngay khi App vừa mở
        refreshNews()
    }

    /**
     * Yêu cầu Repository cập nhật dữ liệu từ Server.
     */
    fun refreshNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null // Xóa lỗi cũ trước khi thử lại

            try {
                // Sử dụng instance của RetrofitClient đã định nghĩa sẵn
                repository.refreshArticles(RetrofitClient.instance)
            } catch (e: Exception) {
                // Xử lý các lỗi ngoại lệ không lường trước (ví dụ: lỗi parsing)
                _errorMessage.value = "Không thể cập nhật tin tức: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Hàm dùng để xóa thông báo lỗi sau khi UI đã hiển thị xong (tránh lặp lại lỗi)
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
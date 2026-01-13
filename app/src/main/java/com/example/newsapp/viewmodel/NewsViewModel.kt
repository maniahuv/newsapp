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
    // Tự động cập nhật UI mỗi khi Database thay đổi (Single Source of Truth).
    val allArticles: LiveData<List<Article>> = repository.allArticles
    val favoriteArticles: LiveData<List<Article>> = repository.favoriteArticles

    // 2. Quản lý trạng thái UI (Loading, Error).
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    // BỔ SUNG: Biến lưu trữ danh mục hiện tại (mặc định là 'general')
    private var currentCategory = "general"

    init {
        // Tự động tải tin tức mới nhất khi App vừa khởi động
        refreshNews()
    }

    /**
     * BỔ SUNG: Cập nhật danh mục mới và tải lại dữ liệu.
     * Được gọi khi người dùng nhấn chuyển Tab trên giao diện.
     */
    fun setCategory(category: String) {
        currentCategory = category
        refreshNews()
    }

    /**
     * Đồng bộ tin tức nóng hổi từ API về Database theo danh mục hiện tại.
     */
    fun refreshNews() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // CẬP NHẬT: Truyền currentCategory vào repository
                repository.refreshArticles(currentCategory)
            } catch (e: Exception) {
                _errorMessage.value = "Không thể kết nối máy chủ: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Xử lý lưu/xóa yêu thích.
     * Quá trình này bao gồm việc Jsoup cào nội dung chi tiết để phục vụ đọc Offline.
     */
    fun toggleFavorite(article: Article) {
        viewModelScope.launch {
            // Hiện loading vì Jsoup cần thời gian cào dữ liệu qua mạng
            _isLoading.value = true
            try {
                repository.toggleFavorite(article)
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi khi xử lý bài báo: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Tìm kiếm tin tức theo từ khóa.
     * Kết quả tìm kiếm từ API sẽ được ghi đè vào Room Database.
     */
    fun searchNews(query: String) {
        // Nếu xóa trắng ô tìm kiếm, tự động quay về tin tức của danh mục hiện tại
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
                _errorMessage.value = "Không tìm thấy kết quả cho: '$query'"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Xóa thông báo lỗi sau khi UI đã hiển thị Toast cho người dùng.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
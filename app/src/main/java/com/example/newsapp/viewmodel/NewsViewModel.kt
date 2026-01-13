package com.example.newsapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.local.Article
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.data.repository.NewsRepository
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    // 1. Lấy danh sách bài báo từ Repository (dữ liệu này lấy từ Room)
    val allArticles: LiveData<List<Article>> = repository.allArticles

    // 2. Biến để theo dõi trạng thái Loading (hiện/ẩn ProgressBar)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // 3. Hàm yêu cầu Repository làm mới tin tức
    fun refreshNews(apiService: NewsApiService) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.refreshArticles(apiService)
            _isLoading.value = false
        }
    }
}
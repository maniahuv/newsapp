package com.example.newsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.data.repository.NewsRepository

/**
 * Factory dùng để khởi tạo NewsViewModel với các tham số truyền vào (Repository).
 * Vì ViewModel mặc định của Android không cho phép truyền tham số vào Constructor,
 * chúng ta cần lớp Factory này để "bơm" Repository vào ViewModel.
 */
class NewsViewModelFactory(private val repository: NewsRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Kiểm tra xem lớp ViewModel yêu cầu có phải là NewsViewModel hay không
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(repository) as T
        }
        // Ném ra ngoại lệ nếu yêu cầu một lớp ViewModel không được hỗ trợ
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
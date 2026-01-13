package com.example.newsapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NewsDao {
    // Lưu một bài báo (nếu trùng URL thì ghi đè)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    // Lấy tất cả tin tức đã lưu trong database
    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    // Lấy danh sách tin tức Yêu thích (isFavorite = true)
    @Query("SELECT * FROM articles WHERE isFavorite = 1")
    fun getFavoriteArticles(): LiveData<List<Article>>

    // Xóa bài báo
    @Delete
    suspend fun deleteArticle(article: Article)
}
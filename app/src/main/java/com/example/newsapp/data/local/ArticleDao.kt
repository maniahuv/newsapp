package com.example.newsapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles ORDER BY publishedAt DESC")
    fun getAllArticles(): LiveData<List<Article>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Nếu trùng URL thì ghi đè tin mới nhất
    suspend fun insertArticles(articles: List<Article>)

    @Query("DELETE FROM articles WHERE isFavorite = 0") // Xóa tin cũ trừ tin yêu thích
    suspend fun deleteOldArticles()
}
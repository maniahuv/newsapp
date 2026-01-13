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

    // Đổi tên từ insertArticles thành insertAll để hết báo đỏ
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<Article>)

    // Đổi tên từ deleteOldArticles thành deleteAll để khớp với Repository
    // Nếu bạn muốn giữ lại các bài báo "Yêu thích", hãy dùng: DELETE FROM articles WHERE isFavorite = 0
    @Query("DELETE FROM articles")
    suspend fun deleteAll()
}
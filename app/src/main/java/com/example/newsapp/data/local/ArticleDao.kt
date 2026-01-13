package com.example.newsapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ArticleDao {
    // Lấy tất cả bài báo, ưu tiên bài mới nhất lên đầu
    @Query("SELECT * FROM articles ORDER BY publishedAt DESC")
    fun getAllArticles(): LiveData<List<Article>>

    // Lấy danh sách các bài báo đã được người dùng nhấn yêu thích
    @Query("SELECT * FROM articles WHERE isFavorite = 1 ORDER BY publishedAt DESC")
    fun getFavoriteArticles(): LiveData<List<Article>>

    // Chèn danh sách bài báo từ API. Nếu trùng URL (khóa chính), sẽ ghi đè dữ liệu mới.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<Article>)

    // Cập nhật trạng thái yêu thích của một bài báo
    @Update
    suspend fun updateArticle(article: Article)

    /**
     * Sửa logic xóa: Chỉ xóa những bài báo KHÔNG phải là mục yêu thích.
     * Điều này giúp khi bạn Refresh tin tức, những bài bạn đã lưu lại không bị mất.
     */
    @Query("DELETE FROM articles WHERE isFavorite = 0")
    suspend fun deleteAllNonFavorites()

    // Nếu bạn thực sự muốn xóa sạch bách (ví dụ: chức năng Log out hoặc Clear Data)
    @Query("DELETE FROM articles")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<Article>)
}
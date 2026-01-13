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

    /**
     * QUAN TRỌNG: Đổi sang OnConflictStrategy.IGNORE.
     * Nếu bài báo đã tồn tại trong Database (dựa trên URL làm khóa chính),
     * hệ thống sẽ KHÔNG chèn đè lên. Điều này giữ nguyên trạng thái 'isFavorite' hiện tại.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(articles: List<Article>)

    // Cập nhật trạng thái yêu thích của một bài báo
    @Update
    suspend fun updateArticle(article: Article)

    /**
     * Chỉ xóa những bài báo KHÔNG phải là mục yêu thích.
     * Điều này giúp khi bạn Refresh tin tức, những bài bạn đã lưu không bị mất khỏi DB.
     */
    @Query("DELETE FROM articles WHERE isFavorite = 0")
    suspend fun deleteAllNonFavorites()

    // Xóa toàn bộ dữ liệu (Dùng khi cần xóa trắng ứng dụng)
    @Query("DELETE FROM articles")
    suspend fun deleteAll()

    /**
     * Hàm phụ trợ dùng cho việc cập nhật nhanh hoặc tìm kiếm.
     * Vẫn giữ IGNORE để bảo vệ trạng thái 'isFavorite'.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticles(articles: List<Article>)
}
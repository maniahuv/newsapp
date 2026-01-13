package com.example.newsapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Article::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {

    // Đổi tên thành getArticleDao để khớp với lời gọi trong MainActivity
    abstract fun getArticleDao(): ArticleDao

    companion object {
        @Volatile
        private var instance: NewsDatabase? = null
        private val LOCK = Any()

        /**
         * Hàm này giúp MainActivity có thể gọi NewsDatabase.getDatabase(this)
         */
        fun getDatabase(context: Context): NewsDatabase {
            return instance ?: synchronized(LOCK) {
                instance ?: createDatabase(context).also { instance = it }
            }
        }

        // Giữ nguyên hàm invoke để bạn có thể dùng NewsDatabase(context) nếu muốn
        operator fun invoke(context: Context) = getDatabase(context)

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                NewsDatabase::class.java,
                "news_db.db"
            )
                .fallbackToDestructiveMigration() // Giúp tránh crash khi bạn thay đổi cấu hình Database
                .build()
    }
}
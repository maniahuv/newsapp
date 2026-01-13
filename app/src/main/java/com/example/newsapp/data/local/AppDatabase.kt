package com.example.newsapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Thêm exportSchema = false để tránh cảnh báo khi build
@Database(entities = [Article::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Nếu INSTANCE đã tồn tại, trả về luôn.
            // Nếu chưa, sẽ tạo mới trong khối synchronized
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "news_database"
                )
                    // fallbackToDestructiveMigration: Khi bạn thay đổi database version,
                    // Room sẽ xóa dữ liệu cũ để tạo bảng mới thay vì báo lỗi crash.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
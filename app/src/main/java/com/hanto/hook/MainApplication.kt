package com.hanto.hook

import android.app.Application
import androidx.room.Room
import com.hanto.hook.database.AppDatabase

class MainApplication : Application() {
    companion object {
        private var instance: MainApplication? = null
        private var database: AppDatabase? = null

        fun getDatabase(): AppDatabase {
            return database ?: throw IllegalStateException("Database is not initialized.")
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration() // 스키마 변경 시 데이터 삭제 옵션
            .build()
    }
}

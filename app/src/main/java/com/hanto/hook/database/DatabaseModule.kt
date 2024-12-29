package com.hanto.hook.database

import android.content.Context
import androidx.room.Room

object DatabaseModule {
    private var instance: AppDatabase? = null

    fun initialize(context: Context) {
        if (instance == null) {
            synchronized(AppDatabase::class) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hook_database"
                ).build()
//                    .fallbackToDestructiveMigration()
            }
        }
    }

    fun getDatabase(): AppDatabase {
        return instance
            ?: throw IllegalStateException("Database is not initialized. Call initialize(context) first.")
    }
}

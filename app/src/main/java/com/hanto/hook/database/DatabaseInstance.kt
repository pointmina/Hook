package com.hanto.hook.database

import android.content.Context
import androidx.room.Room

object DatabaseInstance {
    @Volatile
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            val newInstance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "hook_database"
            ).build()
            instance = newInstance
            newInstance
        }
    }
}

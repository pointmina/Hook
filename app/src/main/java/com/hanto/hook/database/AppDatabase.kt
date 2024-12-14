package com.hanto.hook.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hanto.hook.dao.HookDao
import com.hanto.hook.data.Hook
import com.hanto.hook.data.HookTagMapping
import com.hanto.hook.data.Tag
import com.hanto.hook.util.Converters

@Database(entities = [Hook::class, Tag::class, HookTagMapping::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hookDao(): HookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

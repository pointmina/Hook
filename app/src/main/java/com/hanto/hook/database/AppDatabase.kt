package com.hanto.hook.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hanto.hook.data.dao.HookDao
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.Tag
import com.hanto.hook.util.Converters

@Database(entities = [Hook::class, Tag::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hookDao(): HookDao
}

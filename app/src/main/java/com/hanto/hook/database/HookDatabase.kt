package com.hanto.hook.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hanto.hook.dao.HookDao
import com.hanto.hook.dao.HookTagMappingDao
import com.hanto.hook.dao.TagDao
import com.hanto.hook.data.Hook
import com.hanto.hook.data.HookTagMapping
import com.hanto.hook.data.Tag

@Database(
    entities = [Hook::class, Tag::class, HookTagMapping::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hookDao(): HookDao
    abstract fun tagDao(): TagDao
    abstract fun hookTagMappingDao(): HookTagMappingDao
}

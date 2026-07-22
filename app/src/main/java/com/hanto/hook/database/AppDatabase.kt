package com.hanto.hook.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hanto.hook.data.dao.HookDao
import com.hanto.hook.data.local.entity.HookEntity
import com.hanto.hook.data.local.entity.TagEntity
import com.hanto.hook.util.Converters

@Database(entities = [HookEntity::class, TagEntity::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hookDao(): HookDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Hook ADD COLUMN imageUrl TEXT")
            }
        }

        // Hook.hookId(수정/삭제 조건), Tag.hookId(조인/삭제), Tag.name(태그별 조회/이름변경)에
        // 인덱스를 추가한다. 기존 데이터는 건드리지 않고 인덱스만 생성하므로 안전하다.
        // 인덱스 이름은 Room이 @Entity(indices=...)로부터 자동 생성하는 이름
        // (index_<table>_<column>)과 정확히 일치해야 런타임 스키마 검증을 통과한다.
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_Hook_hookId` ON `Hook` (`hookId`)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_Tag_hookId` ON `Tag` (`hookId`)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_Tag_name` ON `Tag` (`name`)"
                )
            }
        }
    }
}
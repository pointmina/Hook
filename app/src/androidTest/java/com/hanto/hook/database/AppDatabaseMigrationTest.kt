package com.hanto.hook.database

import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * 실제 배포된 v2 스키마 상태를 그대로 재현한 뒤, AppDatabase가 정상적으로 열릴 때
 * MIGRATION_2_3이 기존 데이터를 보존하면서 인덱스를 추가하는지 검증한다.
 *
 * MigrationTestHelper 대신 실제 프로덕션과 동일한 Room.databaseBuilder 경로를 쓴다:
 * Room이 파일을 열면서 버전(2)을 감지하고 등록된 마이그레이션을 실행한 뒤,
 * 컴파일 타임에 생성된 스키마와 결과를 비교해 불일치하면 예외를 던진다.
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val dbName = "migration-test.db"
    private val dbFile: File get() = context.getDatabasePath(dbName)

    @After
    fun tearDown() {
        dbFile.delete()
    }

    @Test
    fun migrate2To3_preservesDataAndAddsIndices() = runBlocking {
        seedVersion2Database()

        val database = Room.databaseBuilder(context, AppDatabase::class.java, dbName)
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
            .build()

        val hook = database.hookDao().getHookById("hook-1")
        assertEquals("title", hook?.title)
        assertEquals("https://example.com", hook?.url)

        val indexNames = mutableSetOf<String>()
        database.openHelper.readableDatabase
            .query("SELECT name FROM sqlite_master WHERE type = 'index'")
            .use { cursor ->
                while (cursor.moveToNext()) {
                    indexNames.add(cursor.getString(0))
                }
            }
        assertTrue(indexNames.contains("index_Hook_hookId"))
        assertTrue(indexNames.contains("index_Tag_hookId"))
        assertTrue(indexNames.contains("index_Tag_name"))

        database.close()
    }

    /** MIGRATION_1_2 이후, 즉 배포 중인 버전 2 스키마를 raw SQL로 그대로 재현한다. */
    private fun seedVersion2Database() {
        dbFile.parentFile?.mkdirs()
        val db = SQLiteDatabase.openOrCreateDatabase(dbFile, null)
        db.execSQL(
            """
            CREATE TABLE Hook (
                id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                hookId TEXT NOT NULL,
                title TEXT NOT NULL,
                url TEXT,
                description TEXT,
                isPinned INTEGER NOT NULL,
                imageUrl TEXT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE Tag (
                tagId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                hookId TEXT NOT NULL,
                name TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            "INSERT INTO Hook (hookId, title, url, description, isPinned, imageUrl) " +
                "VALUES ('hook-1', 'title', 'https://example.com', 'desc', 0, NULL)"
        )
        db.execSQL("INSERT INTO Tag (hookId, name) VALUES ('hook-1', 'kotlin')")
        db.version = 2
        db.close()
    }
}

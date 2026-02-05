package com.hanto.hook.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.HookWithTags
import com.hanto.hook.data.model.Tag
import kotlinx.coroutines.flow.Flow

@Dao
interface HookDao {

    // ---------------------- 삽입 ---------------------- //
    @Insert
    suspend fun insertHook(hook: Hook): Long

    @Insert
    suspend fun insertTag(tag: Tag): Long

    // ---------------------- 삭제 ---------------------- //
    @Query("DELETE FROM Hook WHERE hookId = :hookId")
    suspend fun deleteHookById(hookId: String)

    @Query("DELETE FROM Tag WHERE hookId = :hookId")
    suspend fun deleteTagByHookId(hookId: String)

    @Query("DELETE FROM Tag WHERE name = :tagName")
    suspend fun deleteTagByTagName(tagName: String)

    @Transaction
    suspend fun deleteHookAndTags(hookId: String) {
        deleteTagByHookId(hookId)
        deleteHookById(hookId)
    }

    // ---------------------- 업데이트 ---------------------- //
    @Update
    suspend fun updateHook(hook: Hook)

    @Update
    suspend fun updateTag(tag: Tag)

    @Query("""
        UPDATE Tag 
        SET name = :newTagName 
        WHERE name = :oldTagName
    """)
    suspend fun updateTagName(oldTagName: String, newTagName: String)

    @Query("UPDATE Hook SET isPinned = :isPinned WHERE hookId = :hookId")
    suspend fun updatePinStatus(hookId: String, isPinned: Boolean)

    // ---------------------- 조회 (Flow로 변경) ---------------------- //

    /**
     * 모든 훅과 태그 조회 (Flow)
     */
    @Transaction
    @Query("SELECT * FROM Hook ORDER BY isPinned DESC, id DESC")
    fun getHooksWithTags(): Flow<List<HookWithTags>>

    /**
     * 특정 훅 조회
     */
    @Query("SELECT * FROM Hook WHERE hookId = :hookId")
    suspend fun getHookById(hookId: String): Hook?

    /**
     * 특정 훅의 태그 조회 (Flow)
     */
    @Query("SELECT * FROM Tag WHERE hookId = :hookId")
    fun getTagsForHook(hookId: String): Flow<List<Tag>>

    /**
     * 모든 태그 이름 조회 (Flow)
     */
    @Query("SELECT DISTINCT name FROM Tag ORDER BY name")
    fun getAllTagNames(): Flow<List<String>>

    /**
     * 특정 태그로 훅 조회 (Flow)
     */
    @Transaction
    @Query("""
        SELECT DISTINCT Hook.* FROM Hook 
        INNER JOIN Tag ON Hook.hookId = Tag.hookId 
        WHERE Tag.name = :tagName
        ORDER BY Hook.isPinned DESC, Hook.id DESC
    """)
    fun getHooksByTagName(tagName: String): Flow<List<HookWithTags>>

    @Transaction
    @Query("SELECT * FROM Hook WHERE hookId NOT IN (SELECT hookId FROM Tag) ORDER BY isPinned DESC, id DESC")
    fun getHooksWithNoTags(): Flow<List<HookWithTags>>
}
package com.hanto.hook.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.hanto.hook.data.local.entity.HookEntity
import com.hanto.hook.data.local.entity.TagEntity
import com.hanto.hook.data.local.relation.HookWithTagsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HookDao {

    // ---------------------- 삽입 ---------------------- //
    @Insert
    suspend fun insertHook(hook: HookEntity): Long

    @Insert
    suspend fun insertTag(tag: TagEntity): Long

    @Transaction
    suspend fun insertHookWithTags(hook: HookEntity, tags: List<TagEntity>) {
        insertHook(hook)
        tags.forEach { insertTag(it) }
    }

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
    // 업데이트 기준을 비즈니스 키(hookId)로 삼아, 도메인 모델이 surrogate id에 의존하지 않도록 한다.
    @Query(
        """
        UPDATE Hook
        SET title = :title, url = :url, description = :description, imageUrl = :imageUrl
        WHERE hookId = :hookId
        """
    )
    suspend fun updateHookByHookId(
        hookId: String,
        title: String,
        url: String?,
        description: String?,
        imageUrl: String?
    )

    @Transaction
    suspend fun updateHookWithTags(hook: HookEntity, tags: List<TagEntity>) {
        updateHookByHookId(hook.hookId, hook.title, hook.url, hook.description, hook.imageUrl)
        deleteTagByHookId(hook.hookId)
        tags.forEach { insertTag(it) }
    }

    @Query("UPDATE Tag SET name = :newTagName WHERE name = :oldTagName")
    suspend fun updateTagName(oldTagName: String, newTagName: String)

    @Query("UPDATE Hook SET isPinned = :isPinned WHERE hookId = :hookId")
    suspend fun updatePinStatus(hookId: String, isPinned: Boolean)

    // ---------------------- 조회 ---------------------- //
    @Transaction
    @Query("SELECT * FROM Hook ORDER BY isPinned DESC, id DESC")
    fun getHooksWithTags(): Flow<List<HookWithTagsEntity>>

    @Query("SELECT * FROM Hook WHERE hookId = :hookId")
    suspend fun getHookById(hookId: String): HookEntity?

    @Query("SELECT name FROM Tag WHERE hookId = :hookId")
    fun getTagNamesForHook(hookId: String): Flow<List<String>>

    @Query("SELECT DISTINCT name FROM Tag ORDER BY name")
    fun getAllTagNames(): Flow<List<String>>

    @Transaction
    @Query(
        """
        SELECT DISTINCT Hook.* FROM Hook
        INNER JOIN Tag ON Hook.hookId = Tag.hookId
        WHERE Tag.name = :tagName
        ORDER BY Hook.isPinned DESC, Hook.id DESC
        """
    )
    fun getHooksByTagName(tagName: String): Flow<List<HookWithTagsEntity>>

    @Transaction
    @Query("SELECT * FROM Hook WHERE hookId NOT IN (SELECT hookId FROM Tag) ORDER BY isPinned DESC, id DESC")
    fun getHooksWithNoTags(): Flow<List<HookWithTagsEntity>>
}

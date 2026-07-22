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
    // imageUrl은 의도적으로 제외한다: 썸네일 백필(updateHookImageUrl)이 비동기로 별도
    // 진행되는 동안 사용자가 제목/설명/태그만 수정해도 이 쿼리가 함께 실행되면, 화면에
    // 아직 반영되지 않은 최신 imageUrl을 옛 값(또는 null)으로 덮어써 버리는 레이스가 생긴다.
    @Query(
        """
        UPDATE Hook
        SET title = :title, url = :url, description = :description
        WHERE hookId = :hookId
        """
    )
    suspend fun updateHookByHookId(
        hookId: String,
        title: String,
        url: String?,
        description: String?
    )

    @Transaction
    suspend fun updateHookWithTags(hook: HookEntity, tags: List<TagEntity>) {
        updateHookByHookId(hook.hookId, hook.title, hook.url, hook.description)
        deleteTagByHookId(hook.hookId)
        tags.forEach { insertTag(it) }
    }

    @Query("UPDATE Tag SET name = :newTagName WHERE name = :oldTagName")
    suspend fun updateTagName(oldTagName: String, newTagName: String)

    @Query("UPDATE Hook SET isPinned = :isPinned WHERE hookId = :hookId")
    suspend fun updatePinStatus(hookId: String, isPinned: Boolean)

    // url을 조건에 포함해, 백필이 끝나기 전에 사용자가 URL을 바꿔버린 경우
    // 옛 URL의 썸네일이 새 URL의 레코드에 덮어써지는 것을 막는다.
    @Query(
        """
        UPDATE Hook SET imageUrl = :imageUrl
        WHERE hookId = :hookId AND (url = :url OR (url IS NULL AND :url IS NULL))
        """
    )
    suspend fun updateHookImageUrl(hookId: String, url: String?, imageUrl: String)

    // ---------------------- 조회 ---------------------- //
    @Query("SELECT EXISTS(SELECT 1 FROM Hook LIMIT 1)")
    suspend fun hasAnyHook(): Boolean

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

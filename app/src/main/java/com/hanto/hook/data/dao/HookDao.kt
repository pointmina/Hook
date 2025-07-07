package com.hanto.hook.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.Tag

@Dao
interface HookDao {

    // ---------------------- 삽입 ---------------------- //

    /**
     * 훅을 데이터베이스에 삽입
     * @param hook 삽입할 훅 객체
     * @return 삽입된 훅의 ID
     */
    @Insert
    suspend fun insertHook(hook: Hook): Long

    /**
     * 태그를 데이터베이스에 삽입
     * @param tag 삽입할 태그 객체
     */
    @Insert
    suspend fun insertTag(tag: Tag): Long

    // ---------------------- 삭제 ---------------------- //

    /**
     * ID로 훅을 삭제
     * @param hookId 삭제할 훅의 ID
     */
    @Query("DELETE FROM Hook WHERE hookId = :hookId")
    suspend fun deleteHookById(hookId: String)

    /**
     * Hook ID로 연관된 태그들을 삭제
     * @param hookId 삭제할 훅의 ID
     */
    @Query("DELETE FROM Tag WHERE hookId = :hookId")
    suspend fun deleteTagByHookId(hookId: String)

    /**
     * 이름으로 태그를 삭제
     * @param tagName 삭제할 태그의 이름
     */
    @Query("DELETE FROM Tag WHERE name = :tagName")
    suspend fun deleteTagByTagName(tagName: String)

    /**
     * 훅과 연관된 모든 태그를 함께 삭제
     * Transaction을 사용하여 데이터 일관성을 보장
     */
    @Transaction
    suspend fun deleteHookAndTags(hookId: String) {
        deleteTagByHookId(hookId)
        deleteHookById(hookId)
    }

    // ---------------------- 업데이트 ---------------------- //

    /**
     * 훅을 업데이트
     * @param hook 업데이트할 훅 객체
     */
    @Update
    suspend fun updateHook(hook: Hook)

    /**
     * 태그를 업데이트
     * @param tag 업데이트할 태그 객체
     */
    @Update
    suspend fun updateTag(tag: Tag)

    /**
     * 태그 이름을 일괄 업데이트
     */
    @Query("""
        UPDATE Tag 
        SET name = :newTagName 
        WHERE name = :oldTagName
    """)
    suspend fun updateTagName(oldTagName: String, newTagName: String)

    /**
     * 훅의 고정 상태를 업데이트
     */
    @Query("UPDATE Hook SET isPinned = :isPinned WHERE hookId = :hookId")
    suspend fun updatePinStatus(hookId: String, isPinned: Boolean)

    // ---------------------- 조회 ---------------------- //

    /**
     * 데이터베이스의 모든 훅을 조회 (고정된 항목이 먼저 표시)
     * @return 훅 리스트 LiveData
     */
    @Query("SELECT * FROM Hook ORDER BY isPinned DESC, id DESC")
    fun getAllHooks(): LiveData<List<Hook>>

    /**
     * ID로 특정 훅을 조회
     */
    @Query("SELECT * FROM Hook WHERE hookId = :hookId")
    suspend fun getHookById(hookId: String): Hook?

    /**
     * 특정 훅에 대한 태그를 조회
     * @param hookId 조회할 훅의 ID
     * @return 해당 훅에 관련된 태그 리스트 LiveData
     */
    @Query("SELECT * FROM Tag WHERE hookId = :hookId")
    fun getTagsForHook(hookId: String): LiveData<List<Tag>>

    /**
     * 데이터베이스의 모든 태그 이름을 조회
     * @return 태그 이름 리스트 LiveData
     */
    @Query("SELECT DISTINCT name FROM Tag ORDER BY name")
    fun getAllTagNames(): LiveData<List<String>>

    /**
     * 특정 태그를 가진 훅들을 조회
     */
    @Query("""
        SELECT DISTINCT Hook.* 
        FROM Hook 
        INNER JOIN Tag ON Hook.hookId = Tag.hookId 
        WHERE Tag.name = :tagName
        ORDER BY Hook.isPinned DESC, Hook.id DESC
    """)
    fun getHooksByTagName(tagName: String): LiveData<List<Hook>>
}
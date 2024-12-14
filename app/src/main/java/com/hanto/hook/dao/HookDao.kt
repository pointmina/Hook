package com.hanto.hook.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hanto.hook.data.Hook
import com.hanto.hook.data.HookTagMapping
import com.hanto.hook.data.Tag

@Dao
interface HookDao {

    /**
     * 훅을 데이터베이스에 삽입합니다.
     * @param hook 삽입할 훅 객체
     * @return 삽입된 훅의 ID
     */
    @Insert
    suspend fun insertHook(hook: Hook): Long

    /**
     * 훅을 업데이트합니다.
     * @param hook 업데이트할 훅 객체
     */
    @Update
    suspend fun updateHook(hook: Hook)

    /**
     * ID로 훅을 삭제합니다.
     * @param hookId 삭제할 훅의 ID
     */
    @Query("DELETE FROM Hook WHERE id = :hookId")
    suspend fun deleteHookById(hookId: Long)

    /**
     * 태그를 데이터베이스에 삽입합니다.
     * @param tag 삽입할 태그 객체
     */
    @Insert
    suspend fun insertTag(tag: Tag): Long

    /**
     * 훅과 태그 간의 관계를 데이터베이스에 삽입합니다.
     * @param hookTag 훅-태그 관계 객체
     */
    @Insert
    suspend fun insertMapping(hookTag: HookTagMapping)

    /**
     * 특정 훅 ID의 태그 매핑을 삭제합니다.
     * @param hookId 훅 ID
     */
    @Query("DELETE FROM HookTagMapping WHERE hookId = :hookId")
    suspend fun deleteMappingsByHookId(hookId: Long)

    /**
     * 데이터베이스의 모든 훅을 조회합니다.
     * @return 훅 리스트
     */
    @Query("SELECT * FROM Hook")
    suspend fun getAllHooks(): List<Hook>

    /**
     * 특정 훅에 대한 태그를 조회합니다.
     * @param hookId 조회할 훅의 ID
     * @return 해당 훅에 관련된 태그 리스트
     */
    @Query("SELECT t.name FROM Tag t INNER JOIN HookTagMapping ht ON t.id = ht.tagId WHERE ht.hookId = :hookId")
    suspend fun getTagsForHook(hookId: Long): List<String>

    /**
     * 데이터베이스의 모든 태그 이름을 조회합니다.
     * @return 태그 이름 리스트
     */
    @Query("SELECT name FROM Tag")
    suspend fun getAllTagNames(): List<String>

    /**
     * 주어진 이름의 태그를 데이터베이스에서 조회합니다.
     * @param tagName 조회할 태그의 이름
     * @return 태그 객체 또는 null (태그가 존재하지 않을 경우)
     */
    @Query("SELECT * FROM Tag WHERE name = :tagName LIMIT 1")
    suspend fun getTagByName(tagName: String): Tag?

    /**
     * 주어진 태그 이름에 해당하는 모든 훅을 조회합니다.
     * @param tagName 조회할 태그의 이름
     * @return 해당 태그와 관련된 훅 리스트
     */
    @Transaction
    @Query("""
        SELECT h.* 
        FROM Hook h 
        INNER JOIN HookTagMapping ht ON h.id = ht.hookId 
        INNER JOIN Tag t ON ht.tagId = t.id 
        WHERE t.name = :tagName
    """)
    suspend fun getHooksByTag(tagName: String): List<Hook>
}

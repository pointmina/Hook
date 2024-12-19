package com.hanto.hook.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hanto.hook.data.model.Hook
import com.hanto.hook.data.model.HookTagMapping
import com.hanto.hook.data.model.Tag

@Dao
interface HookDao {


    // ---------------------- 삽입 ---------------------- //

    /**
     * 훅을 데이터베이스에 삽입합니다.
     * @param hook 삽입할 훅 객체
     * @return 삽입된 훅의 ID
     */
    @Insert
    suspend fun insertHook(hook: Hook): Long


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


    // ---------------------- 삭제 ---------------------- //

    /**
     * ID로 훅을 삭제합니다.
     * @param hookId 삭제할 훅의 ID
     */
    @Query("DELETE FROM Hook WHERE hookId = :hookId")
    suspend fun deleteHookById(hookId: String)

    /**
     * ID로 훅을 삭제합니다.
     * @param hookId 삭제할 훅의 ID
     */
    @Query("DELETE FROM Tag WHERE hookId = :hookId")
    suspend fun deleteTagByHookId(hookId: String)

    /**
     * 특정 훅 ID의 태그 매핑을 삭제합니다.
     * @param hookId 훅 ID
     */
    @Query("DELETE FROM HookTagMapping WHERE hookId = :hookId")
    suspend fun deleteMappingsByHookId(hookId: String)

    @Transaction
    @Query("DELETE FROM Hook WHERE hookId = :hookId")
    suspend fun deleteHookAndTags(hookId: String) {
        deleteHookById(hookId)
        deleteTagByHookId(hookId)
        deleteMappingsByHookId(hookId)
    }


    // ---------------------- 업데이트 ---------------------- //

    /**
     * 훅을 업데이트합니다.
     * @param hook 업데이트할 훅 객체
     */
    @Update
    suspend fun updateHook(hook: Hook)

    /**
     * 태그 업데이트합니다.
     * @param tag 업데이트할 태그객체
     */
    @Update
    suspend fun updateTag(tag: Tag)


    // ---------------------- 조회 ---------------------- //

    /**
     * 데이터베이스의 모든 훅을 조회합니다.
     * @return 훅 리스트
     */
    @Query("SELECT * FROM Hook")
    suspend fun getAllHooks(): LiveData<List<Hook>>


    /**
     * 특정 훅에 대한 태그를 조회합니다.
     * @param hookId 조회할 훅의 ID
     * @return 해당 훅에 관련된 태그 리스트
     */
    @Query("SELECT name FROM Tag WHERE hookId = :hookId")
    suspend fun getTagsForHook(hookId: String): LiveData<List<String>>


    /**
     * 데이터베이스의 모든 태그 이름을 조회합니다.
     * @return 태그 이름 리스트
     */
    @Query("SELECT name FROM Tag")
    suspend fun getAllTagNames(): LiveData<List<String>>

    /**
     * 해당 태그를 가진 훅을 조회
     */
    @Query("SELECT * FROM Hook WHERE hookId = :hookId")
    suspend fun getHookByTag(hookId: String): LiveData<List<Hook>?>




}

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
     * 훅을 데이터베이스에 삽입합니다.
     * @param hook 삽입할 훅 객체
     * @return 삽입된 훅의 ID
     */
    @Insert
    fun insertHook(hook: Hook): Long


    /**
     * 태그를 데이터베이스에 삽입합니다.
     * @param tag 삽입할 태그 객체
     */
    @Insert
    fun insertTag(tag: Tag): Long


    // ---------------------- 삭제 ---------------------- //

    /**
     * ID로 훅을 삭제합니다.
     * @param hookId 삭제할 훅의 ID
     */
    @Query("DELETE FROM Hook WHERE hookId = :hookId")
    fun deleteHookById(hookId: String)

    /**
     * ID로 훅을 삭제합니다.
     * @param hookId 삭제할 훅의 ID
     */
    @Query("DELETE FROM Tag WHERE hookId = :hookId")
    fun deleteTagByHookId(hookId: String)


    /**
     * 이름으로 태그를 삭제합니다.
     * @param tagName 삭제할 태그의 이름
     */
    @Query("DELETE FROM Tag WHERE name = :tagName")
    fun deleteTagByTagName(tagName: String)

    @Transaction
    @Query("DELETE FROM Hook WHERE hookId = :hookId")
    fun deleteHookAndTags(hookId: String) {
        deleteHookById(hookId)
        deleteTagByHookId(hookId)
    }


    // ---------------------- 업데이트 ---------------------- //

    /**
     * 훅을 업데이트합니다.
     * @param hook 업데이트할 훅 객체
     */
    @Update
    fun updateHook(hook: Hook)

    /**
     * 태그 업데이트합니다.
     * @param tag 업데이트할 태그객체
     */
    @Update
    fun updateTag(tag: Tag)


    @Query(
        """
        UPDATE Tag 
        SET name = :newTagName 
        WHERE name = :oldTagName
    """
    )
    fun updateTagName(oldTagName: String, newTagName: String)

    @Query("UPDATE Hook SET isPinned = :isPinned WHERE id = :hookId")
    fun updatePinStatus(hookId: String, isPinned: Boolean)


    // ---------------------- 조회 ---------------------- //

    /**
     * 데이터베이스의 모든 훅을 조회합니다.
     * @return 훅 리스트
     */
    @Query("SELECT * FROM Hook ORDER BY isPinned DESC, id DESC")
    fun getAllHooks(): LiveData<List<Hook>>


    @Query("SELECT * FROM Hook WHERE hookId = :hookId")
    fun getHooksByID(hookId: String): Hook


    /**
     * 특정 훅에 대한 태그를 조회합니다.
     * @param hookId 조회할 훅의 ID
     * @return 해당 훅에 관련된 태그 리스트
     */
    @Query("SELECT * FROM Tag WHERE hookId = :hookId")
    fun getTagsForHook(hookId: String): LiveData<List<Tag>>?


    /**
     * 데이터베이스의 모든 태그 이름을 조회합니다.
     * @return 태그 이름 리스트
     */
    @Query("SELECT name FROM Tag")
    fun getAllTagNames(): LiveData<List<String>>

    /**
     * 해당 태그를 가진 훅을 조회
     */
    @Query("SELECT * FROM Hook WHERE hookId = :hookId")
    fun getHookByTag(hookId: String): LiveData<List<Hook>?>

    @Query(
        """
        SELECT Hook.* 
        FROM Hook 
        INNER JOIN Tag ON Hook.hookId = Tag.hookId 
        WHERE Tag.name = :tagName
    """
    )
    fun getHooksByTagName(tagName: String): LiveData<List<Hook>?>


}

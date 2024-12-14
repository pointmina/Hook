package com.hanto.hook.dao

import androidx.room.*
import com.hanto.hook.data.Hook

@Dao
interface HookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHook(hook: Hook): Long

    @Query("SELECT * FROM Hook WHERE hookId = :id")
    suspend fun getHookById(id: Int): Hook

    @Query("SELECT * FROM Hook")
    suspend fun getAllHooks(): List<Hook>
}

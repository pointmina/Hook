package com.hanto.hook.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hanto.hook.data.Hook
import com.hanto.hook.data.HookTagMapping
import com.hanto.hook.data.Tag

@Dao
interface HookTagMappingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMapping(mapping: HookTagMapping)

    @Query("""
        SELECT Hook.* 
        FROM Hook 
        JOIN HookTagMapping ON Hook.hookId = HookTagMapping.hookId 
        JOIN Tag ON Tag.tagId = HookTagMapping.tagId 
        WHERE Tag.name = :tagName
    """)
    suspend fun getHooksByTag(tagName: String): List<Hook>

    @Query("""
        SELECT Tag.* 
        FROM Tag 
        JOIN HookTagMapping ON Tag.tagId = HookTagMapping.tagId 
        WHERE HookTagMapping.hookId = :hookId
    """)
    suspend fun getTagsByHook(hookId: Int): List<Tag>
}

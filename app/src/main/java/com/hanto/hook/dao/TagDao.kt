package com.hanto.hook.dao

import androidx.room.*
import com.hanto.hook.data.Tag

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: Tag): Long

    @Query("SELECT * FROM Tag WHERE tagId = :id")
    suspend fun getTagById(id: Int): Tag

    @Query("SELECT * FROM Tag")
    suspend fun getAllTags(): List<Tag>
}

package com.hanto.hook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room 태그 엔티티. 테이블/컬럼명은 기존 스키마와 동일하게 유지한다.
 */
@Entity(tableName = "Tag")
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val tagId: Long = 0,
    val hookId: String,
    val name: String
)

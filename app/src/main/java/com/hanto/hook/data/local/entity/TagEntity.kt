package com.hanto.hook.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room 태그 엔티티. 테이블/컬럼명은 기존 스키마와 동일하게 유지한다.
 * hookId(조인/삭제 기준), name(태그별 조회/이름변경 기준)에 인덱스를 건다
 * (버전 3, MIGRATION_2_3).
 */
@Entity(
    tableName = "Tag",
    indices = [Index(value = ["hookId"]), Index(value = ["name"])]
)
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val tagId: Long = 0,
    val hookId: String,
    val name: String
)

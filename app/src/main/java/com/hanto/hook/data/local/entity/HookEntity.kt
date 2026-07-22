package com.hanto.hook.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room 훅 엔티티. 테이블/컬럼명은 기존 스키마(version 2)와 동일하게 유지한다.
 * 도메인 모델과 분리된 순수 저장용 모델이며, 불변(val)이다.
 * hookId는 업데이트/삭제 조회의 기준 키라 인덱스를 건다(버전 3, MIGRATION_2_3).
 */
@Entity(tableName = "Hook", indices = [Index(value = ["hookId"])])
data class HookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hookId: String,
    val title: String,
    val url: String?,
    val description: String?,
    val isPinned: Boolean = false,
    val imageUrl: String? = null
)

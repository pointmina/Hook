package com.hanto.hook.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.hanto.hook.data.local.entity.HookEntity
import com.hanto.hook.data.local.entity.TagEntity

/**
 * 훅과 그에 연결된 태그들의 Room 관계 모델. 데이터 레이어 내부에서만 사용한다.
 */
data class HookWithTagsEntity(
    @Embedded val hook: HookEntity,
    @Relation(
        parentColumn = "hookId",
        entityColumn = "hookId"
    )
    val tags: List<TagEntity>
)

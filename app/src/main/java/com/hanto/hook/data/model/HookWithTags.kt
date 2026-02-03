package com.hanto.hook.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class HookWithTags(
    @Embedded val hook: Hook,
    @Relation(
        parentColumn = "hookId",
        entityColumn = "hookId"
    )
    val tags: List<Tag>
)
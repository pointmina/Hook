package com.hanto.hook.data

import androidx.room.Entity

@Entity(
    tableName = "HookTagMapping",
    primaryKeys = ["hookId", "tagId"]
)
data class HookTagMapping(
    val hookId: Long,
    val tagId: Long
)
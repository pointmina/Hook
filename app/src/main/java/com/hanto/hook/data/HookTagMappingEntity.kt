package com.hanto.hook.data

import androidx.room.Entity

@Entity(
    tableName = "HookTagMapping",
    primaryKeys = ["hookId", "tagId"]
)
data class HookTagMapping(
    val hookId: Int,
    val tagId: Int
)

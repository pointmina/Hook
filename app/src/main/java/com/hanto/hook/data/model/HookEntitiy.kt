package com.hanto.hook.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Hook")
data class Hook(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hookId : String,
    val title: String,
    val url: String?,
    val description: String?
)


@Entity(tableName = "Tag")
data class Tag(
    @PrimaryKey(autoGenerate = true) val tagId: Long = 0,
    val hookId: String,
    val name: String
)


@Entity(
    tableName = "HookTagMapping",
    primaryKeys = ["hookId", "tagId"]
)
data class HookTagMapping(
    val hookId: String,
    val tagId: Long
)
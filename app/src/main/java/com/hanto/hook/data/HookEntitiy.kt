package com.hanto.hook.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Hook")
data class Hook(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String?,
    val description: String?
)

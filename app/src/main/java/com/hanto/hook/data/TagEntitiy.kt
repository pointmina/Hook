package com.hanto.hook.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tag")
data class Tag(
    @PrimaryKey(autoGenerate = true) val tagId: Int = 0,
    val name: String
)

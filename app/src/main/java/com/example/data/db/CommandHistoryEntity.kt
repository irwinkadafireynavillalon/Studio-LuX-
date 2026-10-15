package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "command_history")
data class CommandHistoryEntity(
    @PrimaryKey
    val command: String,
    val timestamp: Long = System.currentTimeMillis()
)

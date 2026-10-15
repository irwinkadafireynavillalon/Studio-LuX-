package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val sender: String, // User, Orchestrator, System
    val text: String,
    val timestamp: String,
    val modelName: String
)

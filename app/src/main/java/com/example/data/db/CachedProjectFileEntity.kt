package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_project_files")
data class CachedProjectFileEntity(
    @PrimaryKey
    val id: String, // projectId_fileName
    val projectId: String,
    val fileName: String,
    val path: String,
    val language: String,
    val content: String,
    val lastSyncedTimestamp: Long = System.currentTimeMillis(),
    val isModifiedLocally: Boolean = false
)

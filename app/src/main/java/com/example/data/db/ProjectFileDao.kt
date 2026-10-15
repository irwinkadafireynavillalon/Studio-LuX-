package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectFileDao {

    @Query("SELECT * FROM cached_project_files WHERE projectId = :projectId ORDER BY fileName ASC")
    fun getFilesForProjectFlow(projectId: String): Flow<List<CachedProjectFileEntity>>

    @Query("SELECT * FROM cached_project_files WHERE projectId = :projectId ORDER BY fileName ASC")
    suspend fun getFilesForProject(projectId: String): List<CachedProjectFileEntity>

    @Query("SELECT * FROM cached_project_files WHERE id = :id LIMIT 1")
    suspend fun getFileById(id: String): CachedProjectFileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFiles(files: List<CachedProjectFileEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFile(file: CachedProjectFileEntity)

    @Query("UPDATE cached_project_files SET content = :content, isModifiedLocally = 1, lastSyncedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateFileContent(id: String, content: String, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM cached_project_files WHERE projectId = :projectId")
    suspend fun deleteFilesForProject(projectId: String)

    @Query("SELECT COUNT(*) FROM cached_project_files")
    suspend fun getTotalCachedFilesCount(): Int
}

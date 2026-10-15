package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CommandHistoryDao {
    @Query("SELECT command FROM command_history ORDER BY timestamp ASC")
    fun getCommandHistoryFlow(): Flow<List<String>>

    @Query("SELECT command FROM command_history ORDER BY timestamp ASC")
    suspend fun getCommandHistory(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommand(commandEntity: CommandHistoryEntity)

    @Query("DELETE FROM command_history")
    suspend fun clearHistory()
}

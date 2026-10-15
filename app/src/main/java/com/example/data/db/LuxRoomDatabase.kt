package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CachedProjectFileEntity::class, CommandHistoryEntity::class, MessageEntity::class],
    version = 3,
    exportSchema = false
)
abstract class LuxRoomDatabase : RoomDatabase() {

    abstract fun projectFileDao(): ProjectFileDao
    abstract fun commandHistoryDao(): CommandHistoryDao
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: LuxRoomDatabase? = null

        fun getInstance(context: Context): LuxRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LuxRoomDatabase::class.java,
                    "lux_source_cache.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

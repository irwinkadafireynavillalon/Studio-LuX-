package com.example.data.db

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    companion object {
        val TTS_VOLUME = floatPreferencesKey("tts_volume")
    }

    val ttsVolume: Flow<Float> = context.dataStore.data
        .map { preferences ->
            preferences[TTS_VOLUME] ?: 1.0f
        }

    suspend fun saveTtsVolume(volume: Float) {
        context.dataStore.edit { preferences ->
            preferences[TTS_VOLUME] = volume
        }
    }
}

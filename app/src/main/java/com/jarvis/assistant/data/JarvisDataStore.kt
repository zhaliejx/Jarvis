package com.jarvis.assistant.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private const val JARVIS_PREFERENCES_NAME = "jarvis_preferences"
private val Context.dataStore by preferencesDataStore(name = JARVIS_PREFERENCES_NAME)

class JarvisDataStore(private val context: Context) {
    companion object {
        @Volatile
        private var instance: JarvisDataStore? = null

        fun getInstance(context: Context): JarvisDataStore {
            return instance ?: synchronized(this) {
                JarvisDataStore(context.applicationContext).also { instance = it }
            }
        }
    }

    suspend fun savePreference(key: String, value: String) {
        val preferenceKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferenceKey] = value
        }
    }

    suspend fun getPreference(key: String): String? {
        val preferenceKey = stringPreferencesKey(key)
        return context.dataStore.data.map { preferences ->
            preferences[preferenceKey]
        }.let { flow ->
            var result: String? = null
            flow.collect { result = it }
            result
        }
    }

    suspend fun getAllPreferences(): Map<String, String> {
        return context.dataStore.data.map { preferences ->
            preferences.asMap().mapKeys { it.key.name }.mapValues { it.value as String }
        }.let { flow ->
            var result = mapOf<String, String>()
            flow.collect { result = it }
            result
        }
    }
}

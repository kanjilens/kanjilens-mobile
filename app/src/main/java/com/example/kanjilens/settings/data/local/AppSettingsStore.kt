package com.example.kanjilens.settings.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "kanjilens_settings")

data class AppSettings(
    val darkMode: Boolean = false,
    val language: String = "Portugues"
)

object AppSettingsStore {
    private val darkModeKey = booleanPreferencesKey("dark_mode")
    private val languageKey = stringPreferencesKey("language")

    fun settingsFlow(context: Context): Flow<AppSettings> {
        val appContext = context.applicationContext
        return appContext.settingsDataStore.data.map { preferences ->
            AppSettings(
                darkMode = preferences[darkModeKey] ?: false,
                language = preferences[languageKey] ?: "Portugues"
            )
        }
    }

    suspend fun updateDarkMode(context: Context, enabled: Boolean) {
        val appContext = context.applicationContext
        appContext.settingsDataStore.edit { preferences ->
            preferences[darkModeKey] = enabled
        }
    }

    suspend fun updateLanguage(context: Context, language: String) {
        val appContext = context.applicationContext
        appContext.settingsDataStore.edit { preferences ->
            preferences[languageKey] = language
        }
    }
}

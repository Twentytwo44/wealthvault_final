package com.example.setting_app

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.core.KoinConst
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class SettingsManager(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        private val LANGUAGE = stringPreferencesKey("app_language")
    }

    // ดึงค่า Setting (เช่น Theme)
    val isDarkMode: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[IS_DARK_MODE] ?: false }

    // ฟังก์ชันเปลี่ยนค่า Setting
    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = enabled
        }
    }

    suspend fun setLanguage(lang: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE] = lang
        }
    }
}
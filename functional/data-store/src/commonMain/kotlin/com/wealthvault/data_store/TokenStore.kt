package com.wealthvault.data_store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class TokenStore(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    // ดึง Access Token
    val accessToken: Flow<String?> = dataStore.data
        .map { preferences -> preferences[KEY_ACCESS_TOKEN] }

    // ดึง Refresh Token
    val refreshToken: Flow<String?> = dataStore.data
        .map { preferences -> preferences[KEY_REFRESH_TOKEN] }

    // ฟังก์ชันบันทึกทั้งคู่พร้อมกัน (สะดวกตอน Login และ Refresh)
    suspend fun saveTokens(access: String, refresh: String) {
        dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = access
            preferences[KEY_REFRESH_TOKEN] = refresh
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}

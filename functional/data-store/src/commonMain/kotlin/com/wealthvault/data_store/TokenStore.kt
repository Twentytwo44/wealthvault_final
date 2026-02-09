package com.wealthvault.data_store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class TokenStore(private val dataStore: DataStore<Preferences>) {

    companion object {
        val KEY_TOKEN = stringPreferencesKey("auth_token")
    }

    // อ่านค่า Token (Return เป็น Flow เพื่อให้ Reactive)
    val authToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[KEY_TOKEN]
        }

    // บันทึกค่า Token
    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token
        }
    }

    // ลบค่า (Logout)
    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}

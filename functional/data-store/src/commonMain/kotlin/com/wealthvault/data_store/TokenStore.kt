package com.wealthvault.data_store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


data class AuthToken(val accessToken: String?, val refreshToken: String?)

data class UserId(val userId: String?)

data class DeviceInfo( val fcmToken: String?, val platform: String?, val deviceName: String?)

class TokenStore(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")


        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_FCM_TOKEN = stringPreferencesKey("fcm_token")
        private val KEY_PLATFORM = stringPreferencesKey("platform")
        private val KEY_DEVICE_NAME = stringPreferencesKey("device_name")

    }


    // token
    val accessToken: Flow<String?> = dataStore.data
        .map { preferences -> preferences[KEY_ACCESS_TOKEN] }

    val fcmToken: Flow<String?> = dataStore.data
        .map { preferences -> preferences[KEY_FCM_TOKEN] }

    val refreshToken: Flow<String?> = dataStore.data
        .map { preferences -> preferences[KEY_REFRESH_TOKEN] }

//    suspend fun saveTokens(access: String, refresh: String) {
//        dataStore.edit { preferences ->
//            preferences[KEY_ACCESS_TOKEN] = access
//            preferences[KEY_REFRESH_TOKEN] = refresh
//        }
//    }

    // --- ส่วนของ Token ---
    val authData: Flow<AuthToken> = dataStore.data.map { pref ->
        AuthToken(pref[KEY_ACCESS_TOKEN], pref[KEY_REFRESH_TOKEN])
    }

    suspend fun saveAuthToken(token: AuthToken) {
        dataStore.edit { pref ->
            pref[KEY_ACCESS_TOKEN] = token.accessToken ?: ""
            pref[KEY_REFRESH_TOKEN] = token.refreshToken ?: ""
        }
    }

    // device info
    val deviceInfo: Flow<DeviceInfo> = dataStore.data.map { pref ->
        DeviceInfo(
            fcmToken = pref[KEY_FCM_TOKEN],
            platform = pref[KEY_PLATFORM],
            deviceName = pref[KEY_DEVICE_NAME]
        )
    }
    suspend fun saveUserId(device: UserId) {
        dataStore.edit { pref ->
            pref[KEY_USER_ID] = device.userId ?: ""

        }
    }


    suspend fun saveDeviceInfo(device: DeviceInfo) {
        dataStore.edit { pref ->
            pref[KEY_FCM_TOKEN] = device.fcmToken ?: ""
            pref[KEY_PLATFORM] = device.platform ?: ""
            pref[KEY_DEVICE_NAME] = device.deviceName ?: ""
        }
    }



    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}

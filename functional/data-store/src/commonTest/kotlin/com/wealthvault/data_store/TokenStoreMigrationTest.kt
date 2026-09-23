package com.wealthvault.data_store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.wealthvault.domain.auth.SessionState
import com.wealthvault.domain.auth.SessionDeviceInfo
import com.wealthvault.domain.auth.SessionTokens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TokenStoreMigrationTest {
    @Test
    fun legacyTokensAreVerifiedInSecureStorageBeforeLegacyRemoval() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = "legacy-access"
            preferences[REFRESH_TOKEN] = "legacy-refresh"
        }
        val secureStorage = FakeSecureStorage()
        val store = TokenStore(dataStore, secureStorage)

        assertEquals("legacy-access", store.accessToken.first())
        assertEquals("legacy-refresh", store.refreshToken.first())
        assertEquals("legacy-access", secureStorage.values[SECURE_ACCESS_TOKEN])
        assertEquals("legacy-refresh", secureStorage.values[SECURE_REFRESH_TOKEN])
        assertEquals(null, dataStore.data.first()[ACCESS_TOKEN])
        assertEquals(null, dataStore.data.first()[REFRESH_TOKEN])
        assertEquals(SessionState.Authenticated, store.status.value)
    }

    @Test
    fun secureStoreFailureKeepsLegacySessionForRetry() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        dataStore.edit { preferences -> preferences[ACCESS_TOKEN] = "legacy-access" }
        val secureStorage = FakeSecureStorage(failWrites = true)
        val store = TokenStore(dataStore, secureStorage)

        assertEquals("legacy-access", store.accessToken.first())
        assertEquals("legacy-access", dataStore.data.first()[ACCESS_TOKEN])

        secureStorage.failWrites = false
        assertEquals("legacy-access", store.accessToken.first())
        assertEquals("legacy-access", secureStorage.values[SECURE_ACCESS_TOKEN])
        assertEquals(null, dataStore.data.first()[ACCESS_TOKEN])
    }

    @Test
    fun secureReadBackMismatchKeepsLegacySessionUntilRetrySucceeds() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        dataStore.edit { preferences -> preferences[ACCESS_TOKEN] = "legacy-access" }
        val secureStorage = FakeSecureStorage(corruptWrites = true)
        val store = TokenStore(dataStore, secureStorage)

        assertEquals("legacy-access", store.accessToken.first())
        assertEquals("legacy-access", dataStore.data.first()[ACCESS_TOKEN])

        secureStorage.corruptWrites = false
        assertEquals("legacy-access", store.accessToken.first())
        assertEquals("legacy-access", secureStorage.values[SECURE_ACCESS_TOKEN])
        assertEquals(null, dataStore.data.first()[ACCESS_TOKEN])
    }

    @Test
    fun savingAndClearingTokensUsesSecureStorageAndUpdatesSessionState() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        val secureStorage = FakeSecureStorage()
        val store = TokenStore(dataStore, secureStorage)

        store.saveTokens(SessionTokens("access", "refresh"))
        assertEquals("access", store.accessToken.first())
        assertEquals("refresh", store.refreshToken.first())
        assertEquals(com.wealthvault.data_store.SessionState.Authenticated, store.sessionState.value)
        assertEquals("access", secureStorage.values[SECURE_ACCESS_TOKEN])
        assertEquals("refresh", secureStorage.values[SECURE_REFRESH_TOKEN])

        store.saveDeviceInfo(SessionDeviceInfo("fcm", "android", "Pixel"))
        assertEquals("fcm", store.deviceInfo.first().fcmToken)
        assertEquals("android", store.deviceInfo.first().platform)
        assertEquals("Pixel", store.deviceInfo.first().deviceName)
        store.saveUserId(UserId("user-1"))
        assertEquals("user-1", store.getUserId.first())

        store.saveTokens(SessionTokens(null, null))
        assertEquals(com.wealthvault.data_store.SessionState.SignedOut, store.sessionState.value)
        assertNull(secureStorage.values[SECURE_ACCESS_TOKEN])
        assertNull(secureStorage.values[SECURE_REFRESH_TOKEN])
        store.clear()
        assertNull(store.getUserId.first())
        assertNull(store.deviceInfo.first().fcmToken)
    }

    @Test
    fun secureReadFailureLeavesLegacyValuesAuthoritative() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        dataStore.edit { preferences -> preferences[ACCESS_TOKEN] = "legacy-access" }
        val secureStorage = FakeSecureStorage(failReads = true)
        val store = TokenStore(dataStore, secureStorage)

        assertEquals("legacy-access", store.accessToken.first())
        assertEquals("legacy-access", dataStore.data.first()[ACCESS_TOKEN])
        secureStorage.failReads = false
        assertEquals("legacy-access", store.accessToken.first())
    }

    private class FakeSecureStorage(
        var failWrites: Boolean = false,
        var corruptWrites: Boolean = false,
        var failReads: Boolean = false,
    ) : SecureStorage {
        val values = mutableMapOf<String, String>()

        override suspend fun read(key: String): String? {
            check(!failReads) { "secure storage unavailable" }
            return values[key]
        }

        override suspend fun write(key: String, value: String) {
            check(!failWrites) { "secure storage unavailable" }
            values[key] = if (corruptWrites) "corrupted-value" else value
        }

        override suspend fun remove(key: String) {
            values.remove(key)
        }

        override suspend fun clear() {
            values.clear()
        }
    }

    private class InMemoryPreferencesDataStore : DataStore<Preferences> {
        private val state = MutableStateFlow<Preferences>(emptyPreferences())

        override val data: Flow<Preferences> = state

        override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
            val updated = transform(state.value)
            state.value = updated
            return updated
        }
    }

    private companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        const val SECURE_ACCESS_TOKEN = "access_token"
        const val SECURE_REFRESH_TOKEN = "refresh_token"
    }
}

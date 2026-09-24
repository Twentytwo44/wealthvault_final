package com.wealthvault.security.session

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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
        assertEquals("13:legacy-access14:legacy-refresh", secureStorage.values[SECURE_SESSION_BUNDLE])
        assertEquals(null, dataStore.data.first()[ACCESS_TOKEN])
        assertEquals(null, dataStore.data.first()[REFRESH_TOKEN])
        assertEquals(SessionState.Authenticated, store.status.value)
    }

    @Test
    fun committedSecurePairWinsOverStaleLegacyValuesAfterRestart() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = "old-access"
            preferences[REFRESH_TOKEN] = "old-refresh"
        }
        val secureStorage = FakeSecureStorage()
        secureStorage.values[SECURE_SESSION_BUNDLE] = "10:new-access11:new-refresh"
        secureStorage.values[SECURE_ACCESS_TOKEN] = "new-access"
        secureStorage.values[SECURE_REFRESH_TOKEN] = "new-refresh"
        val store = TokenStore(dataStore, secureStorage)

        assertEquals("new-access", store.accessToken.first())
        assertEquals("new-refresh", store.refreshToken.first())
        assertEquals(SessionState.Authenticated, store.status.value)
        assertNull(dataStore.data.first()[ACCESS_TOKEN])
        assertNull(dataStore.data.first()[REFRESH_TOKEN])
    }

    @Test
    fun committedSignedOutPairDoesNotResurrectStaleLegacySession() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = "old-access"
            preferences[REFRESH_TOKEN] = "old-refresh"
        }
        val secureStorage = FakeSecureStorage()
        secureStorage.values[SECURE_SESSION_BUNDLE] = "-1:-1:"
        val store = TokenStore(dataStore, secureStorage)

        assertNull(store.accessToken.first())
        assertNull(store.refreshToken.first())
        assertEquals(SessionState.SignedOut, store.status.value)
        assertNull(dataStore.data.first()[ACCESS_TOKEN])
        assertNull(dataStore.data.first()[REFRESH_TOKEN])
    }

    @Test
    fun committedPairKeepsLiveSessionUsableWhenLegacyCleanupTemporarilyFails() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        val secureStorage = FakeSecureStorage()
        val store = TokenStore(dataStore, secureStorage)
        store.saveTokens(SessionTokens("old-access", "old-refresh"))

        dataStore.failUpdates = true
        assertFailsWith<IllegalStateException> {
            store.saveTokens(SessionTokens("new-access", "new-refresh"))
        }

        dataStore.failUpdates = false
        assertEquals("new-access", store.accessToken.first())
        assertEquals("new-refresh", store.refreshToken.first())
        assertNull(dataStore.data.first()[ACCESS_TOKEN])
        assertNull(dataStore.data.first()[REFRESH_TOKEN])
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
        assertEquals(SessionState.Authenticated, store.status.value)
        assertEquals("access", secureStorage.values[SECURE_ACCESS_TOKEN])
        assertEquals("refresh", secureStorage.values[SECURE_REFRESH_TOKEN])

        store.saveDeviceInfo(SessionDeviceInfo("fcm", "android", "Pixel"))
        assertEquals("fcm", store.fcmToken.first())
        assertEquals("android", dataStore.data.first()[PLATFORM])
        assertEquals("Pixel", dataStore.data.first()[DEVICE_NAME])
        store.saveUserId("user-1")
        assertEquals("user-1", store.getUserId.first())

        store.saveTokens(SessionTokens(null, null))
        assertEquals(SessionState.SignedOut, store.status.value)
        assertNull(secureStorage.values[SECURE_ACCESS_TOKEN])
        assertNull(secureStorage.values[SECURE_REFRESH_TOKEN])
        store.clear()
        assertNull(store.getUserId.first())
        assertEquals("fcm", store.fcmToken.first())
        assertEquals("android", dataStore.data.first()[PLATFORM])
        assertEquals("Pixel", dataStore.data.first()[DEVICE_NAME])
    }

    @Test
    fun clearingSessionDoesNotEraseUnrelatedPreferences() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        val unrelatedKey = stringPreferencesKey("unrelated_preference")
        dataStore.edit { preferences -> preferences[unrelatedKey] = "keep-me" }
        val store = TokenStore(dataStore, FakeSecureStorage())

        store.saveTokens(SessionTokens("access", "refresh"))
        store.saveDeviceInfo(SessionDeviceInfo("fcm", "ios", "iPhone"))
        store.clear()

        assertEquals("keep-me", dataStore.data.first()[unrelatedKey])
        assertEquals("fcm", store.fcmToken.first())
        assertEquals("ios", dataStore.data.first()[PLATFORM])
    }

    @Test
    fun clearingSessionPreservesSecretsOwnedByAnotherCapability() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        val secureStorage = FakeSecureStorage()
        secureStorage.values["push-private-key"] = "must-survive"
        val store = TokenStore(dataStore, secureStorage)

        store.saveTokens(SessionTokens("access", "refresh"))
        store.clearTokens()

        assertEquals("must-survive", secureStorage.values["push-private-key"])
        assertNull(secureStorage.values[SECURE_ACCESS_TOKEN])
        assertNull(secureStorage.values[SECURE_REFRESH_TOKEN])
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

    @Test
    fun concurrentTokenMutationsDoNotOverlapSecureWrites() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        val secureStorage = TrackingSecureStorage()
        val store = TokenStore(dataStore, secureStorage)

        listOf(
            async { store.saveTokens(SessionTokens("access-a", "refresh-a")) },
            async { store.saveTokens(SessionTokens("access-b", "refresh-b")) },
        ).awaitAll()

        assertEquals(1, secureStorage.maxConcurrentWrites)
        assertEquals(store.accessToken.first(), secureStorage.values[SECURE_ACCESS_TOKEN])
        assertEquals(store.refreshToken.first(), secureStorage.values[SECURE_REFRESH_TOKEN])
    }

    @Test
    fun partialTokenWriteRollsBackToThePreviousSession() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        val secureStorage = FakeSecureStorage()
        val store = TokenStore(dataStore, secureStorage)

        store.saveTokens(SessionTokens("old-access", "old-refresh"))
        secureStorage.failWritesForKey = SECURE_REFRESH_TOKEN

        assertFailsWith<IllegalStateException> {
            store.saveTokens(SessionTokens("new-access", "new-refresh"))
        }

        assertEquals("old-access", secureStorage.values[SECURE_ACCESS_TOKEN])
        assertEquals("old-refresh", secureStorage.values[SECURE_REFRESH_TOKEN])
        assertEquals("old-access", store.accessToken.first())
        assertEquals("old-refresh", store.refreshToken.first())
    }

    @Test
    fun partialLogoutRollsBackToThePreviousSession() = runTest {
        val dataStore = InMemoryPreferencesDataStore()
        val secureStorage = FakeSecureStorage()
        val store = TokenStore(dataStore, secureStorage)

        store.saveTokens(SessionTokens("old-access", "old-refresh"))
        store.saveUserId("user-1")
        secureStorage.failRemovesForKey = SECURE_REFRESH_TOKEN

        assertFailsWith<IllegalStateException> {
            store.clearTokens()
        }

        assertEquals("old-access", secureStorage.values[SECURE_ACCESS_TOKEN])
        assertEquals("old-refresh", secureStorage.values[SECURE_REFRESH_TOKEN])
        assertEquals("old-access", store.accessToken.first())
        assertEquals("old-refresh", store.refreshToken.first())
        assertEquals("user-1", store.getUserId.first())
    }

    private class FakeSecureStorage(
        var failWrites: Boolean = false,
        var corruptWrites: Boolean = false,
        var failReads: Boolean = false,
        var failWritesForKey: String? = null,
        var failRemovesForKey: String? = null,
    ) : SecureStorage {
        val values = mutableMapOf<String, String>()

        override suspend fun read(key: String): String? {
            check(!failReads) { "secure storage unavailable" }
            return values[key]
        }

        override suspend fun write(key: String, value: String) {
            check(!failWrites && failWritesForKey != key) { "secure storage unavailable" }
            values[key] = if (corruptWrites) "corrupted-value" else value
        }

        override suspend fun remove(key: String) {
            check(failRemovesForKey != key) { "secure storage unavailable" }
            values.remove(key)
        }

        override suspend fun clear() {
            values.clear()
        }
    }

    private class TrackingSecureStorage : SecureStorage {
        val values = mutableMapOf<String, String>()
        var maxConcurrentWrites = 0
        private var activeWrites = 0

        override suspend fun read(key: String): String? = values[key]

        override suspend fun write(key: String, value: String) {
            activeWrites += 1
            maxConcurrentWrites = maxOf(maxConcurrentWrites, activeWrites)
            delay(1)
            values[key] = value
            activeWrites -= 1
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
        var failUpdates = false

        override val data: Flow<Preferences> = state

        override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
            check(!failUpdates) { "preference store unavailable" }
            val updated = transform(state.value)
            state.value = updated
            return updated
        }
    }

    private companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val PLATFORM = stringPreferencesKey("platform")
        val DEVICE_NAME = stringPreferencesKey("device_name")
        const val SECURE_ACCESS_TOKEN = "access_token"
        const val SECURE_REFRESH_TOKEN = "refresh_token"
        const val SECURE_SESSION_BUNDLE = "session_token_pair"
    }
}

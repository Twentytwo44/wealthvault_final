package com.wealthvault.data_store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.wealthvault.domain.auth.SessionDeviceInfo
import com.wealthvault.domain.auth.SessionManager as DomainSessionManager
import com.wealthvault.domain.auth.SessionState as DomainSessionState
import com.wealthvault.domain.auth.SessionTokenStore
import com.wealthvault.domain.auth.SessionTokens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


data class AuthToken(val accessToken: String?, val refreshToken: String?)

data class UserId(val userId: String?)

data class DeviceInfo( val fcmToken: String?, val platform: String?, val deviceName: String?)

class TokenStore(
    private val dataStore: DataStore<Preferences>,
    private val secureStorage: SecureStorage,
) : SessionManager, DomainSessionManager, SessionTokenStore {

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")


        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_FCM_TOKEN = stringPreferencesKey("fcm_token")
        private val KEY_PLATFORM = stringPreferencesKey("platform")
        private val KEY_DEVICE_NAME = stringPreferencesKey("device_name")

        private const val SECURE_ACCESS_TOKEN = "access_token"
        private const val SECURE_REFRESH_TOKEN = "refresh_token"

    }

    private val authState = MutableStateFlow(AuthToken(null, null))
    private val _sessionState = MutableStateFlow(SessionState.Loading)
    override val sessionState: StateFlow<SessionState> = _sessionState
    private val _domainSessionState = MutableStateFlow(DomainSessionState.Loading)
    override val status: StateFlow<DomainSessionState> = _domainSessionState
    private val loadMutex = Mutex()
    private var isLoaded = false
    private var migrationPending = false

    private suspend fun ensureAuthLoaded() {
        if (isLoaded && !migrationPending) return
        loadMutex.withLock {
            if (isLoaded && !migrationPending) return

            val secureAccessRead = readSecureValue(SECURE_ACCESS_TOKEN)
            val secureRefreshRead = readSecureValue(SECURE_REFRESH_TOKEN)
            val secureAccess = secureAccessRead.value
            val secureRefresh = secureRefreshRead.value
            val legacy = dataStore.data.first()
            val legacyAccess = legacy[KEY_ACCESS_TOKEN]
            val legacyRefresh = legacy[KEY_REFRESH_TOKEN]
            // A legacy value remains authoritative until secure storage has
            // been written and verified. This prevents a corrupted or stale
            // read-back value from silently replacing a valid session.
            val access = legacyAccess?.takeIf { it != secureAccess } ?: secureAccess
            val refresh = legacyRefresh?.takeIf { it != secureRefresh } ?: secureRefresh
            authState.value = AuthToken(access, refresh)
            _sessionState.value = if (!access.isNullOrBlank()) {
                SessionState.Authenticated
            } else {
                SessionState.SignedOut
            }
            _domainSessionState.value = if (!access.isNullOrBlank()) {
                DomainSessionState.Authenticated
            } else {
                DomainSessionState.SignedOut
            }

            // Migrate only after each secure value has been written and read back.
            // If a platform keystore/keychain is temporarily unavailable, keep the
            // legacy values so the next access can retry without logging the user out.
            val accessMigrated = migrateValue(
                secureKey = SECURE_ACCESS_TOKEN,
                secureValue = secureAccess,
                legacyValue = legacyAccess,
                secureReadSucceeded = secureAccessRead.succeeded,
            )
            val refreshMigrated = migrateValue(
                secureKey = SECURE_REFRESH_TOKEN,
                secureValue = secureRefresh,
                legacyValue = legacyRefresh,
                secureReadSucceeded = secureRefreshRead.succeeded,
            )
            migrationPending = !accessMigrated || !refreshMigrated
            if (!migrationPending && (!access.isNullOrBlank() || !refresh.isNullOrBlank())) {
                dataStore.edit { preferences ->
                    preferences.remove(KEY_ACCESS_TOKEN)
                    preferences.remove(KEY_REFRESH_TOKEN)
                }
            }
            isLoaded = true
        }
    }

    private suspend fun migrateValue(
        secureKey: String,
        secureValue: String?,
        legacyValue: String?,
        secureReadSucceeded: Boolean,
    ): Boolean {
        if (!secureReadSucceeded) return false
        if (legacyValue.isNullOrBlank() || secureValue == legacyValue) return true
        return try {
            secureStorage.write(secureKey, legacyValue)
            secureStorage.read(secureKey) == legacyValue
        } catch (error: Throwable) {
            if (error is kotlinx.coroutines.CancellationException) throw error
            false
        }
    }

    private suspend fun readSecureValue(key: String): SecureReadResult = try {
        SecureReadResult(secureStorage.read(key), succeeded = true)
    } catch (error: Throwable) {
        if (error is kotlinx.coroutines.CancellationException) throw error
        // Keep the legacy session available and retry on the next access. A
        // transient Keychain/Keystore outage must not force a logout.
        SecureReadResult(value = null, succeeded = false)
    }

    private data class SecureReadResult(val value: String?, val succeeded: Boolean)


    // token
    override val accessToken: Flow<String?> = authState
        .onStart { ensureAuthLoaded() }
        .map { it.accessToken }

    override val getUserId: Flow<String?> = dataStore.data
        .map { preferences -> preferences[KEY_USER_ID] }

    override val fcmToken: Flow<String?> = dataStore.data
        .map { preferences -> preferences[KEY_FCM_TOKEN] }

    override val refreshToken: Flow<String?> = authState
        .onStart { ensureAuthLoaded() }
        .map { it.refreshToken }

    // --- ส่วนของ Token ---
    override val authData: Flow<AuthToken> = authState.onStart { ensureAuthLoaded() }

    override suspend fun saveAuthToken(token: AuthToken) {
        ensureAuthLoaded()
        if (token.accessToken.isNullOrBlank()) {
            secureStorage.remove(SECURE_ACCESS_TOKEN)
        } else {
            persistSecureValue(SECURE_ACCESS_TOKEN, token.accessToken)
        }
        if (token.refreshToken.isNullOrBlank()) {
            secureStorage.remove(SECURE_REFRESH_TOKEN)
        } else {
            persistSecureValue(SECURE_REFRESH_TOKEN, token.refreshToken)
        }
        dataStore.edit { pref ->
            pref.remove(KEY_ACCESS_TOKEN)
            pref.remove(KEY_REFRESH_TOKEN)
        }
        authState.value = token
        _sessionState.value = if (!token.accessToken.isNullOrBlank()) {
            SessionState.Authenticated
        } else {
            SessionState.SignedOut
        }
        _domainSessionState.value = if (!token.accessToken.isNullOrBlank()) {
            DomainSessionState.Authenticated
        } else {
            DomainSessionState.SignedOut
        }
        migrationPending = false
    }

    override suspend fun saveTokens(tokens: SessionTokens) {
        saveAuthToken(AuthToken(tokens.accessToken, tokens.refreshToken))
    }

    private suspend fun persistSecureValue(key: String, value: String) {
        secureStorage.write(key, value)
        check(secureStorage.read(key) == value) {
            "Secure session write could not be verified"
        }
    }

    // device info
    override val deviceInfo: Flow<DeviceInfo> = dataStore.data.map { pref ->
        DeviceInfo(
            fcmToken = pref[KEY_FCM_TOKEN],
            platform = pref[KEY_PLATFORM],
            deviceName = pref[KEY_DEVICE_NAME]
        )
    }
    override suspend fun saveUserId(device: UserId) {
        dataStore.edit { pref ->
            if (device.userId.isNullOrBlank()) {
                pref.remove(KEY_USER_ID)
            } else {
                pref[KEY_USER_ID] = device.userId
            }

        }
    }

    /** Domain-facing identity mutation; the legacy value object stays adapter-local. */
    override suspend fun saveUserId(userId: String?) {
        saveUserId(UserId(userId))
    }


    override suspend fun saveDeviceInfo(device: DeviceInfo) {
        dataStore.edit { pref ->
            if (device.fcmToken.isNullOrBlank()) pref.remove(KEY_FCM_TOKEN)
            else pref[KEY_FCM_TOKEN] = device.fcmToken
            if (device.platform.isNullOrBlank()) pref.remove(KEY_PLATFORM)
            else pref[KEY_PLATFORM] = device.platform
            if (device.deviceName.isNullOrBlank()) pref.remove(KEY_DEVICE_NAME)
            else pref[KEY_DEVICE_NAME] = device.deviceName
        }
    }

    override suspend fun saveDeviceInfo(device: SessionDeviceInfo) {
        saveDeviceInfo(
            DeviceInfo(
                fcmToken = device.fcmToken,
                platform = device.platform,
                deviceName = device.deviceName,
            ),
        )
    }



    override suspend fun clear() {
        secureStorage.clear()
        dataStore.edit { it.clear() }
        authState.value = AuthToken(null, null)
        _sessionState.value = SessionState.SignedOut
        _domainSessionState.value = DomainSessionState.SignedOut
        isLoaded = true
        migrationPending = false
    }

    override suspend fun clearTokens() {
        clear()
    }
}

package com.wealthvault.security.session

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


private data class TokenPair(val accessToken: String?, val refreshToken: String?)

class TokenStore(
    private val dataStore: DataStore<Preferences>,
    private val secureStorage: SecureStorage,
) : DomainSessionManager, SessionTokenStore {

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")


        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_FCM_TOKEN = stringPreferencesKey("fcm_token")
        private val KEY_PLATFORM = stringPreferencesKey("platform")
        private val KEY_DEVICE_NAME = stringPreferencesKey("device_name")

        private const val SECURE_ACCESS_TOKEN = "access_token"
        private const val SECURE_REFRESH_TOKEN = "refresh_token"
        /**
         * Verified token-pair commit record. The individual keys remain for
         * backward compatibility with installs that predate this record.
         */
        private const val SECURE_SESSION_BUNDLE = "session_token_pair"

    }

    private val authState = MutableStateFlow(TokenPair(null, null))
    private val _domainSessionState = MutableStateFlow(DomainSessionState.Loading)
    override val status: StateFlow<DomainSessionState> = _domainSessionState
    private val loadMutex = Mutex()
    /** Serializes secure-token writes/clears so refresh and logout cannot interleave. */
    private val mutationMutex = Mutex()
    private var isLoaded = false
    private var migrationPending = false

    private suspend fun ensureAuthLoaded() {
        if (isLoaded && !migrationPending) return
        loadMutex.withLock {
            if (isLoaded && !migrationPending) return

            val secureBundleRead = readSecureValue(SECURE_SESSION_BUNDLE)
            val legacy = dataStore.data.first()
            val legacyAccess = legacy[KEY_ACCESS_TOKEN]
            val legacyRefresh = legacy[KEY_REFRESH_TOKEN]

            // The pair record is written and verified before legacy cleanup.
            // If the process stopped between those operations, prefer the
            // committed secure pair instead of resurrecting stale plaintext.
            val committedPair = secureBundleRead.value?.let(::decodeTokenPair)
            if (secureBundleRead.succeeded && secureBundleRead.value != null && committedPair != null) {
                val normalized = committedPair.normalized()
                authState.value = normalized
                updateSessionState(normalized)
                migrationPending = try {
                    dataStore.edit { preferences ->
                        preferences.remove(KEY_ACCESS_TOKEN)
                        preferences.remove(KEY_REFRESH_TOKEN)
                    }
                    false
                } catch (error: Throwable) {
                    if (error is kotlinx.coroutines.CancellationException) throw error
                    // Keep the secure commit authoritative and retry only the
                    // harmless legacy cleanup on the next access.
                    true
                }
                isLoaded = true
                return
            }

            val secureAccessRead = readSecureValue(SECURE_ACCESS_TOKEN)
            val secureRefreshRead = readSecureValue(SECURE_REFRESH_TOKEN)
            val secureAccess = secureAccessRead.value
            val secureRefresh = secureRefreshRead.value
            // A legacy value remains authoritative until secure storage has
            // been written and verified. This prevents a corrupted or stale
            // read-back value from silently replacing a valid session.
            val selected = TokenPair(
                accessToken = legacyAccess?.takeIf { it != secureAccess } ?: secureAccess,
                refreshToken = legacyRefresh?.takeIf { it != secureRefresh } ?: secureRefresh,
            ).normalized()
            authState.value = selected
            updateSessionState(selected)

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
            val pairMigrated = if (!secureBundleRead.succeeded) {
                false
            } else if (selected.accessToken.isNullOrBlank() && selected.refreshToken.isNullOrBlank()) {
                true
            } else {
                migratePair(selected)
            }
            migrationPending = !accessMigrated || !refreshMigrated || !pairMigrated
            if (!migrationPending && (!selected.accessToken.isNullOrBlank() || !selected.refreshToken.isNullOrBlank())) {
                dataStore.edit { preferences ->
                    preferences.remove(KEY_ACCESS_TOKEN)
                    preferences.remove(KEY_REFRESH_TOKEN)
                }
            }
            isLoaded = true
        }
    }

    private suspend fun migratePair(pair: TokenPair): Boolean = try {
        val encoded = encodeTokenPair(pair)
        secureStorage.write(SECURE_SESSION_BUNDLE, encoded)
        secureStorage.read(SECURE_SESSION_BUNDLE) == encoded
    } catch (error: Throwable) {
        if (error is kotlinx.coroutines.CancellationException) throw error
        false
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

    private suspend fun saveTokenPair(token: TokenPair) = mutationMutex.withLock {
        ensureAuthLoaded()
        val normalized = token.normalized()
        // A token pair is one logical session. Snapshot both values before
        // touching either key so a partial Keystore/Keychain failure cannot
        // leave access and refresh tokens from different sessions on disk.
        val previousBundle = secureStorage.read(SECURE_SESSION_BUNDLE)
        val previousAccess = secureStorage.read(SECURE_ACCESS_TOKEN)
        val previousRefresh = secureStorage.read(SECURE_REFRESH_TOKEN)
        try {
            // Commit the verified pair first. If the process stops before
            // DataStore cleanup, startup can recover this pair atomically.
            persistSecureValue(SECURE_SESSION_BUNDLE, encodeTokenPair(normalized))
            persistOrRemoveSecureValue(SECURE_ACCESS_TOKEN, normalized.accessToken)
            persistOrRemoveSecureValue(SECURE_REFRESH_TOKEN, normalized.refreshToken)
        } catch (error: Throwable) {
            if (error is kotlinx.coroutines.CancellationException) throw error
            // Best-effort rollback preserves the last known-good pair. The
            // original write error remains the one surfaced to the caller.
            try {
                restoreSecureValue(SECURE_SESSION_BUNDLE, previousBundle)
            } catch (rollbackError: Throwable) {
                if (rollbackError is kotlinx.coroutines.CancellationException) throw rollbackError
            }
            try {
                restoreSecureValue(SECURE_ACCESS_TOKEN, previousAccess)
            } catch (rollbackError: Throwable) {
                if (rollbackError is kotlinx.coroutines.CancellationException) throw rollbackError
                // Keep the original failure as the public result.
            }
            try {
                restoreSecureValue(SECURE_REFRESH_TOKEN, previousRefresh)
            } catch (rollbackError: Throwable) {
                if (rollbackError is kotlinx.coroutines.CancellationException) throw rollbackError
                // Keep the original failure as the public result.
            }
            throw error
        }
        try {
            dataStore.edit { pref ->
                pref.remove(KEY_ACCESS_TOKEN)
                pref.remove(KEY_REFRESH_TOKEN)
            }
        } catch (error: Throwable) {
            if (error is kotlinx.coroutines.CancellationException) throw error
            // The secure pair is already committed. Keep the live process on
            // the new session and retry only the legacy cleanup on next read.
            authState.value = normalized
            updateSessionState(normalized)
            migrationPending = true
            throw error
        }
        authState.value = normalized
        updateSessionState(normalized)
        migrationPending = false
    }

    override suspend fun saveTokens(tokens: SessionTokens) {
        saveTokenPair(TokenPair(tokens.accessToken, tokens.refreshToken))
    }

    private suspend fun persistSecureValue(key: String, value: String) {
        secureStorage.write(key, value)
        check(secureStorage.read(key) == value) {
            "Secure session write could not be verified"
        }
    }

    private suspend fun persistOrRemoveSecureValue(key: String, value: String?) {
        if (value.isNullOrBlank()) secureStorage.remove(key)
        else persistSecureValue(key, value)
    }

    private suspend fun restoreSecureValue(key: String, value: String?) {
        if (value.isNullOrBlank()) {
            secureStorage.remove(key)
        } else {
            // Do not call persistSecureValue here: a failing read-back should
            // not hide the original write error while rolling back.
            secureStorage.write(key, value)
        }
    }

    override suspend fun saveUserId(userId: String?) {
        dataStore.edit { pref ->
            if (userId.isNullOrBlank()) {
                pref.remove(KEY_USER_ID)
            } else {
                pref[KEY_USER_ID] = userId
            }

        }
    }

    override suspend fun saveDeviceInfo(device: SessionDeviceInfo) {
        val fcmToken = device.fcmToken
        val platform = device.platform
        val deviceName = device.deviceName
        dataStore.edit { pref ->
            if (fcmToken.isNullOrBlank()) pref.remove(KEY_FCM_TOKEN)
            else pref[KEY_FCM_TOKEN] = fcmToken
            if (platform.isNullOrBlank()) pref.remove(KEY_PLATFORM)
            else pref[KEY_PLATFORM] = platform
            if (deviceName.isNullOrBlank()) pref.remove(KEY_DEVICE_NAME)
            else pref[KEY_DEVICE_NAME] = deviceName
        }
    }



    override suspend fun clear() = mutationMutex.withLock {
        ensureAuthLoaded()
        // Session logout must not erase unrelated secrets owned by another
        // security capability. Remove only session identity and token keys;
        // device metadata is deliberately retained so a later authenticated
        // session can re-register the same push token without losing the
        // platform state during logout.
        // Treat the pair and its legacy fallback as one logical operation.
        // If a platform keystore/keychain operation or the preference update
        // fails halfway through, restore the previous secure pair and leave
        // the in-memory session untouched so logout cannot create a partial
        // session that refresh code would interpret inconsistently.
        val previousBundle = secureStorage.read(SECURE_SESSION_BUNDLE)
        val previousAccess = secureStorage.read(SECURE_ACCESS_TOKEN)
        val previousRefresh = secureStorage.read(SECURE_REFRESH_TOKEN)
        try {
            secureStorage.remove(SECURE_SESSION_BUNDLE)
            secureStorage.remove(SECURE_ACCESS_TOKEN)
            secureStorage.remove(SECURE_REFRESH_TOKEN)
            dataStore.edit { preferences ->
                preferences.remove(KEY_ACCESS_TOKEN)
                preferences.remove(KEY_REFRESH_TOKEN)
                preferences.remove(KEY_USER_ID)
            }
        } catch (error: Throwable) {
            if (error is kotlinx.coroutines.CancellationException) throw error
            // Best-effort rollback preserves the last known-good pair. The
            // original clear error remains the one surfaced to the caller.
            try {
                restoreSecureValue(SECURE_SESSION_BUNDLE, previousBundle)
            } catch (rollbackError: Throwable) {
                if (rollbackError is kotlinx.coroutines.CancellationException) throw rollbackError
            }
            try {
                restoreSecureValue(SECURE_ACCESS_TOKEN, previousAccess)
            } catch (rollbackError: Throwable) {
                if (rollbackError is kotlinx.coroutines.CancellationException) throw rollbackError
            }
            try {
                restoreSecureValue(SECURE_REFRESH_TOKEN, previousRefresh)
            } catch (rollbackError: Throwable) {
                if (rollbackError is kotlinx.coroutines.CancellationException) throw rollbackError
            }
            throw error
        }
        authState.value = TokenPair(null, null)
        _domainSessionState.value = DomainSessionState.SignedOut
        isLoaded = true
        migrationPending = false
    }

    override suspend fun clearTokens() {
        clear()
    }

    private fun updateSessionState(token: TokenPair) {
        _domainSessionState.value = if (!token.accessToken.isNullOrBlank()) {
            DomainSessionState.Authenticated
        } else {
            DomainSessionState.SignedOut
        }
    }

    private fun TokenPair.normalized(): TokenPair = TokenPair(
        accessToken = accessToken?.takeUnless { it.isBlank() },
        refreshToken = refreshToken?.takeUnless { it.isBlank() },
    )

    /** Length-prefixed encoding keeps arbitrary token characters lossless. */
    private fun encodeTokenPair(pair: TokenPair): String =
        encodeTokenPart(pair.accessToken) + encodeTokenPart(pair.refreshToken)

    private fun encodeTokenPart(value: String?): String =
        value?.let { "${it.length}:$it" } ?: "-1:"

    private fun decodeTokenPair(encoded: String): TokenPair? {
        var offset = 0

        fun readPart(): ParsedPart? {
            val separator = encoded.indexOf(':', offset)
            if (separator < 0) return null
            val length = encoded.substring(offset, separator).toIntOrNull() ?: return null
            offset = separator + 1
            if (length == -1) return ParsedPart(null)
            if (length < 0 || offset + length > encoded.length) return null
            val value = encoded.substring(offset, offset + length)
            offset += length
            return ParsedPart(value)
        }

        val access = readPart() ?: return null
        val refresh = readPart() ?: return null
        if (offset != encoded.length) return null
        return TokenPair(access.value, refresh.value).normalized()
    }

    private data class ParsedPart(val value: String?)
}

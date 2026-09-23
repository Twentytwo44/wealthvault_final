package com.wealthvault.data_store

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.content.Context
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
private const val KEY_ALIAS = "wealthvault_session_key"
private const val PREFS_NAME = "wealthvault_secure_session"
private const val TRANSFORMATION = "AES/GCM/NoPadding"

class AndroidSecureStorage(context: Context) : SecureStorage {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val keyLock = Any()

    override suspend fun read(key: String): String? = withContext(Dispatchers.IO) {
        preferences.getString(key, null)?.let(::decrypt)
    }

    override suspend fun write(key: String, value: String) = withContext(Dispatchers.IO) {
        check(preferences.edit().putString(key, encrypt(value)).commit()) {
            "Unable to persist secure session value"
        }
    }

    override suspend fun remove(key: String) = withContext(Dispatchers.IO) {
        check(preferences.edit().remove(key).commit()) {
            "Unable to remove secure session value"
        }
    }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        check(preferences.edit().clear().commit()) {
            "Unable to clear secure session values"
        }
    }

    private fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val iv = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
        val encrypted = Base64.encodeToString(
            cipher.doFinal(value.toByteArray(StandardCharsets.UTF_8)),
            Base64.NO_WRAP,
        )
        return "$iv:$encrypted"
    }

    private fun decrypt(value: String): String? = runCatching {
        val parts = value.split(':', limit = 2)
        require(parts.size == 2)
        val iv = Base64.decode(parts[0], Base64.NO_WRAP)
        val encrypted = Base64.decode(parts[1], Base64.NO_WRAP)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(128, iv))
        cipher.doFinal(encrypted).toString(StandardCharsets.UTF_8)
    }.getOrNull()

    private fun secretKey(): SecretKey {
        synchronized(keyLock) {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
            val existing = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
            if (existing != null) return existing

            val generator = KeyGenerator.getInstance("AES", KEYSTORE_PROVIDER)
            // AndroidKeyStore requires the purpose and block-mode contract to
            // be supplied through KeyGenParameterSpec. Calling init(Int)
            // delegates to the generic JCA provider and fails at runtime on
            // API 23+ with "Cannot initialize without a ... parameter".
            generator.init(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                )
                    .setKeySize(256)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(true)
                    .build(),
            )
            return generator.generateKey()
        }
    }
}

package com.wealthvault.security.session

/** Platform-backed storage for credentials and other secrets. */
interface SecureStorage {
    suspend fun read(key: String): String?
    suspend fun write(key: String, value: String)
    suspend fun remove(key: String)
    suspend fun clear()
}

package com.wealthvault.data_store

import kotlinx.coroutines.flow.Flow

/** Boundary used by networking and features; the storage implementation can change independently. */
interface SessionStore {
    val accessToken: Flow<String?>
    val refreshToken: Flow<String?>
    val authData: Flow<AuthToken>
    val getUserId: Flow<String?>
    val fcmToken: Flow<String?>
    val deviceInfo: Flow<DeviceInfo>

    suspend fun saveAuthToken(token: AuthToken)
    suspend fun saveUserId(device: UserId)
    suspend fun saveDeviceInfo(device: DeviceInfo)
    suspend fun clear()
}

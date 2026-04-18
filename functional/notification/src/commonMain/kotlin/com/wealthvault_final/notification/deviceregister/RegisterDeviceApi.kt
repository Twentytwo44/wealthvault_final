package com.wealthvault_final.notification.deviceregister

interface RegisterDeviceApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}

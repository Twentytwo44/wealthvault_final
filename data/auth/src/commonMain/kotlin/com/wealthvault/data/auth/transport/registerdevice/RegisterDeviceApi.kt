package com.wealthvault.data.auth.transport.registerdevice

import com.wealthvault.domain.notification.DeviceMutationResult

internal interface RegisterDeviceApi {
    suspend fun registerDevice(
        token: String?,
        platform: String?,
        deviceName: String?,
    ): DeviceMutationResult
}

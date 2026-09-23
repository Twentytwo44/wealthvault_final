package com.wealthvault.notification_api.registerdevice

import com.wealthvault.domain.notification.DeviceMutationResult

interface AddDevicesApi {
    suspend fun addDevices(
        token: String?,
        platform: String?,
        deviceName: String?,
    ): DeviceMutationResult
}

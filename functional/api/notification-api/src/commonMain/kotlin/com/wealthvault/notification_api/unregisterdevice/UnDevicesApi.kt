package com.wealthvault.notification_api.unregisterdevice

import com.wealthvault.domain.notification.DeviceMutationResult

interface UnDevicesApi {
    suspend fun unDevices(token: String): DeviceMutationResult
}

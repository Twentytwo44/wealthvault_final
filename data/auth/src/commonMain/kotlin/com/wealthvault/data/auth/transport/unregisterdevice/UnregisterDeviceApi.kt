package com.wealthvault.data.auth.transport.unregisterdevice

import com.wealthvault.domain.notification.DeviceMutationResult

internal interface UnregisterDeviceApi {
    suspend fun unregisterDevice(token: String): DeviceMutationResult
}

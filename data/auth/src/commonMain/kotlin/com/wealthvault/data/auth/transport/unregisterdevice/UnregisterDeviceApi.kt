package com.wealthvault.data.auth.transport.unregisterdevice

import com.wealthvault.core.model.DeviceMutationResult

internal interface UnregisterDeviceApi {
    suspend fun unregisterDevice(token: String): DeviceMutationResult
}

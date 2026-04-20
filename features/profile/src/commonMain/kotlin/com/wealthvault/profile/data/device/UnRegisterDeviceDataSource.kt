package com.wealthvault.profile.data.device

import com.wealthvault.notification_api.model.UnDeviceRequest
import com.wealthvault.notification_api.unregisterdevice.UnDevicesApi

class UnRegisterDeviceDataSource(
    private val unDevicesApi: UnDevicesApi,
) {
    suspend fun unDevice(request: UnDeviceRequest): Result<String> {
        return runCatching {
            val result = unDevicesApi.unDevices(request)
            result.message  ?: throw IllegalArgumentException("Token is null")
        }
    }
}

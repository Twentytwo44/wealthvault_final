package com.wealthvault.login.data.device

import com.wealthvault.notification_api.model.DeviceRequest
import com.wealthvault.notification_api.registerdevice.AddDevicesApi

class RegisterDeviceDataSource(
    private val registerDeviceApi: AddDevicesApi,
) {
    suspend fun addDevice(request: DeviceRequest): Result<String> {
        return runCatching {
            val result = registerDeviceApi.addDevices(request)
            result.message  ?: throw IllegalArgumentException("Token is null")
        }
    }
}

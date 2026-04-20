package com.wealthvault.login.data.device

import com.wealthvault.notification_api.model.DeviceRequest

class RegisterDeviceRepositoryImpl (
    private val registerDeviceDataSource: RegisterDeviceDataSource
)  {
        suspend fun addDevice(request: DeviceRequest): Result<String> {
            return registerDeviceDataSource.addDevice(request).map { data ->
                data
            }
        }

}

package com.wealthvault.profile.data.device

import com.wealthvault.notification_api.model.UnDeviceRequest

class UnRegisterDeviceRepositoryImpl (
    private val unRegisterDeviceDataSource: UnRegisterDeviceDataSource
)  {
    suspend fun unDevice(request: UnDeviceRequest): Result<String> {
        return unRegisterDeviceDataSource.unDevice(request).map { data ->
            data
        }
    }

}

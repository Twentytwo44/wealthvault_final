package com.wealthvault.notification_api.getalldevice

import com.wealthvault.domain.notification.DeviceInfo

interface GetAllDeviceApi {
    suspend fun getAllDevices(): List<DeviceInfo>
}

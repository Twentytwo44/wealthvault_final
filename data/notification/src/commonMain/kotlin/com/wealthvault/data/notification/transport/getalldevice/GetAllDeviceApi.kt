package com.wealthvault.data.notification.transport.getalldevice

import com.wealthvault.domain.notification.DeviceInfo

interface GetAllDeviceApi {
    suspend fun getAllDevices(): List<DeviceInfo>
}

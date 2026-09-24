package com.wealthvault.data.notification.transport

import com.wealthvault.domain.notification.DeviceInfo
import com.wealthvault.data.notification.transport.model.DeviceItem
import com.wealthvault.data.notification.transport.model.GetDeviceResponse

internal fun GetDeviceResponse.toDomain(): List<DeviceInfo> = data.orEmpty().map(DeviceItem::toDomain)

private fun DeviceItem.toDomain() = DeviceInfo(
    id = id,
    userId = userId,
    token = token,
    platform = platform,
    deviceName = deviceName,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

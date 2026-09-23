package com.wealthvault.notification_api

import com.wealthvault.domain.notification.DeviceInfo
import com.wealthvault.domain.notification.DeviceMutationResult
import com.wealthvault.notification_api.model.DeviceItem
import com.wealthvault.notification_api.model.DeviceResponse
import com.wealthvault.notification_api.model.GetDeviceResponse

internal fun GetDeviceResponse.toDomain(): List<DeviceInfo> = data.orEmpty().map(DeviceItem::toDomain)

internal fun DeviceResponse.toDomain() = DeviceMutationResult(
    message = message,
    success = success,
)

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

package com.wealthvault.notification_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class DeviceRequest(
    @SerialName("token")
    val token: String? = null,

    @SerialName("platform")
    val platform: String? = null,

    @SerialName("device_name")
    val deviceName: String? = null,
)

@Serializable
data class UnDeviceRequest(
    @SerialName("token")
    val token: String? = null,
)


@Serializable
data class GetDeviceResponse(
    @SerialName("data")
    val data: List<DeviceItem>? = null,

    @SerialName("success")
    val success: Boolean? = null
)

@Serializable
data class DeviceItem(
    @SerialName("ID")
    val id: String? = null,

    @SerialName("UserID")
    val userId: String? = null,

    @SerialName("Token")
    val token: String? = null,

    @SerialName("Platform")
    val platform: String? = null,

    @SerialName("DeviceName")
    val deviceName: String? = null,

    @SerialName("IsActive")
    val isActive: Boolean? = null,

    @SerialName("CreatedAt")
    val createdAt: String? = null,

    @SerialName("UpdatedAt")
    val updatedAt: String? = null
)



@Serializable
data class DeviceResponse(
    @SerialName("message")
    val message: String? = null,

    @SerialName("success")
    val success: Boolean? = null
)

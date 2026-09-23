package com.wealthvault.data.auth.transport.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Remote payloads for device registration; never exposed outside data:auth. */
@Serializable
internal data class RegisterDeviceRequest(
    @SerialName("token") val token: String? = null,
    @SerialName("platform") val platform: String? = null,
    @SerialName("device_name") val deviceName: String? = null,
)

@Serializable
internal data class UnregisterDeviceRequest(
    @SerialName("token") val token: String? = null,
)

@Serializable
internal data class DeviceMutationResponse(
    @SerialName("message") val message: String? = null,
    @SerialName("success") val success: Boolean? = null,
)

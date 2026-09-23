package com.wealthvault.data.auth.transport.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OTPRequest(
    val email: String,
    val otp: String
)
@Serializable
internal data class OTPResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: OTPData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
internal data class OTPData(
    @SerialName("success")
    val success: Boolean,

    @SerialName("reset_token")
    val resetToken: String,
)

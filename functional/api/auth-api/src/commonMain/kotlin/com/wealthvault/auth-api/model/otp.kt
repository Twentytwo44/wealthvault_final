package com.wealthvault.`auth-api`.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OTPRequest(
    val email: String,
    val otp: String
)
@Serializable
data class OTPResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: OTPData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class OTPData(
    @SerialName("success")
    val success: Boolean,

    @SerialName("reset_token")
    val resetToken: String,
)

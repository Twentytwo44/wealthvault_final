package com.wealthvault.`auth-api`.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForgetPasswordRequest(
    val email: String,
)
@Serializable
data class ForgetPasswordResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: ForgetPasswordData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class ForgetPasswordData(
    @SerialName("success")
    val success: Boolean,

)

package com.wealthvault.`auth-api`.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ResetPasswordRequest(
    val resettoken: String,
    val password: String
)
@Serializable
internal data class ResetPasswordResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: ResetPasswordData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
internal data class ResetPasswordData(
    @SerialName("success")
    val success: Boolean,

)

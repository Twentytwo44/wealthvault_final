package com.wealthvault.`auth-api`.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenRequest(
    @SerialName("token")
    val token: String,

)
@Serializable
data class TokenResponse(
    @SerialName("line_id")
    val lineId: String? = null,

    @SerialName("message")
    val message: String? = null,

    @SerialName("success")
    val success: Boolean? = null,

    @SerialName("error")
    val error: String? = null
)


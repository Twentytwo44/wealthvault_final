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
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: String? = null,

    @SerialName("error")
    val error: String? = null
)


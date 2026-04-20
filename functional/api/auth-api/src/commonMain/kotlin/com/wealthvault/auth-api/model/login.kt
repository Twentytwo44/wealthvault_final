package com.wealthvault.`auth-api`.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)
@Serializable
data class LoginResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: LoginData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class LoginData(
    @SerialName("success")
    val success: Boolean? = null,

    @SerialName("access_token")
    val accessToken: String? = null,

    @SerialName("refresh_token")
    val refreshToken: String? = null,

    @SerialName("user_id")
    val userId: String? = null
)

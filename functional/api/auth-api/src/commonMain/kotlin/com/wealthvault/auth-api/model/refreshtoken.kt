package com.wealthvault.`auth-api`.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RefreshRequest(
    val refreshtoken : String
)
@Serializable
data class RefreshResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: RefreshData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class RefreshData(
    @SerialName("success")
    val success: Boolean,

    @SerialName("access_token")
    val accessToken: String,

    @SerialName("refresh_token")
    val refreshToken: String,

    @SerialName("user_id")
    val userId: String
)

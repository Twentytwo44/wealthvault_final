package com.wealthvault.data.auth.transport.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class RefreshRequest(
    val refreshtoken : String
)
@Serializable
internal data class RefreshResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: RefreshData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
internal data class RefreshData(
    @SerialName("success")
    val success: Boolean,

    @SerialName("access_token")
    val accessToken: String,

    @SerialName("refresh_token")
    val refreshToken: String,

    @SerialName("user_id")
    val userId: String
)

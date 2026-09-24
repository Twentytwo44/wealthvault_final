package com.wealthvault.data.auth.transport.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RegisterRequest(
    val email: String,
    val password: String
)
@Serializable
internal data class RegisterResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: LoginData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
internal data class RegisterData(
    @SerialName("user_id")
    val userId: String,
)

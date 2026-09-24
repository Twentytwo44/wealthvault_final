package com.wealthvault.data.auth.transport.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ForgetPasswordRequest(
    val email: String,
)
@Serializable
internal data class ForgetPasswordResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: ForgetPasswordData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
internal data class ForgetPasswordData(
    @SerialName("success")
    val success: Boolean,

)

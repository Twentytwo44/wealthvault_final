package com.example.cash_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class DeleteCashResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: DeleteCashData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class DeleteCashData(
    @SerialName("success")
    val success: String,



)



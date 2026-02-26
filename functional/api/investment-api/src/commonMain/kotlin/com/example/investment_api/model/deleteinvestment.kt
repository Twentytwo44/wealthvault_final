package com.example.investment_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class DeleteInvestmentResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: DeleteInvestmentData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class DeleteInvestmentData(
    @SerialName("success")
    val success: String,



)



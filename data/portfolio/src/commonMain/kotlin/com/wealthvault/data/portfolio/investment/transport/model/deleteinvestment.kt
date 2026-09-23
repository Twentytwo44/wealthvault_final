package com.wealthvault.data.portfolio.investment.transport.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class DeleteInvestmentResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: DeleteInvestmentData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
internal data class DeleteInvestmentData(
    @SerialName("success")
    val success: String? = null,



)


package com.wealthvault.data.portfolio.cash.transport.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class DeleteCashResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: DeleteCashData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
internal data class DeleteCashData(
    @SerialName("success")
    val success: String? = null,



)


package com.wealthvault.data.portfolio.liability.transport.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class DeleteLiabilityResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: DeleteLiabilityData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
internal data class DeleteLiabilityData(
    @SerialName("success")
    val success: String? = null,



)


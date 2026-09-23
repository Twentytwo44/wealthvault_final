package com.wealthvault.data.portfolio.insurance.transport.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class DeleteInsuranceResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: DeleteInsuranceData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
internal data class DeleteInsuranceData(
    @SerialName("success")
    val success: String? = null,



)


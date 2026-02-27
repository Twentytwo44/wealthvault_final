package com.example.liability_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class DeleteLiabilityResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: DeleteLiabilityData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class DeleteLiabilityData(
    @SerialName("success")
    val success: String,



)



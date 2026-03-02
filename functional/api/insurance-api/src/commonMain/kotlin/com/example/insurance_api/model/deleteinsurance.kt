package com.example.insurance_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class DeleteInsuranceResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: DeleteInsuranceData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class DeleteInsuranceData(
    @SerialName("success")
    val success: String,



)



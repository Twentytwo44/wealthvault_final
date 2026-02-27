package com.example.land_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class DeleteLandResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: DeleteLandData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class DeleteLandData(
    @SerialName("success")
    val success: String,



)



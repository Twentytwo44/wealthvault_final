package com.example.building_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class DeleteBuildingResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: DeleteBuildingData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class DeleteBuildingData(
    @SerialName("success")
    val success: String,



)



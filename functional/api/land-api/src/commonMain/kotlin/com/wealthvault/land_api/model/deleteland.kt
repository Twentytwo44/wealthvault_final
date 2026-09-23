package com.wealthvault.land_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class DeleteLandResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: DeleteLandData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
internal data class DeleteLandData(
    @SerialName("success")
    val success: String? = null,



)


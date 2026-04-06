package com.wealthvault.liability_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LiabilityRequest(

    @SerialName("type")
    val type: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("creditor")
    val creditor: String? = null,

    @SerialName("principal")
    val principal: Double? = null,

    @SerialName("interest_rate")
    val interestRate: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("started_at")
    val startedAt: String? = null,

    @SerialName("ended_at")
    val endedAt: String? = null,

    @SerialName("files")
    val files: List<LiabilityUploadData> = emptyList()



    )

@Serializable
data class LiabilityResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: LiabilityData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class LiabilityData(
    @SerialName("id")
    val id: String? = null,

    @SerialName("user_id")
    val userId: String? = null,

    @SerialName("type")
    val type: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("creditor")
    val creditor: String? = null,

    @SerialName("principal")
    val principal: Int? = null,

    @SerialName("interest_rate")
    val interestRate: Double? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("started_at")
    val startedAt: String? = null,

    @SerialName("ended_at")
    val endedAt: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,


)

@Serializable
data class LiabilityUploadData(
    val bytes: ByteArray,
    val mimeType: String,
    val fileName: String
)

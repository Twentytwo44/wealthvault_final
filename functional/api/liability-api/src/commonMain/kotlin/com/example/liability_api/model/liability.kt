package com.example.liability_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LiabilityRequest(
    @SerialName("id")
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("type")
    val type: String,

    @SerialName("name")
    val name: String,

    @SerialName("creditor")
    val creditor: String,

    @SerialName("principal")
    val principal: String,

    @SerialName("interest_rate")
    val interestRate: String,

    @SerialName("description")
    val description: String,

    @SerialName("started_at")
    val startedAt: String,

    @SerialName("ended_at")
    val endedAt: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String,




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
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("type")
    val type: String,

    @SerialName("name")
    val name: String,

    @SerialName("creditor")
    val creditor: String,

    @SerialName("principal")
    val principal: Int,

    @SerialName("interest_rate")
    val interestRate: Double,

    @SerialName("description")
    val description: String,

    @SerialName("started_at")
    val startedAt: String,

    @SerialName("ended_at")
    val endedAt: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String,




)


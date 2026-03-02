package com.example.investment_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class InvestmentRequest(
    @SerialName("id")
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("name")
    val name: String,

    @SerialName("symbol")
    val symbol: String,

    @SerialName("type")
    val type: String,

    @SerialName("broker_name")
    val brokerName: String,

    @SerialName("quantity")
    val quantity: Double,

    @SerialName("cost_per_price")
    val costPerPrice: Double,

    @SerialName("description")
    val description: String,

)

@Serializable
data class InvestmentResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: InvestmentData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class InvestmentData(
    @SerialName("id")
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("name")
    val name: String,

    @SerialName("symbol")
    val symbol: String,

    @SerialName("type")
    val type: String,

    @SerialName("broker_name")
    val brokerName: String,

    @SerialName("quantity")
    val quantity: Double,

    @SerialName("cost_per_price")
    val costPerPrice: Double,

    @SerialName("description")
    val description: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String,



)



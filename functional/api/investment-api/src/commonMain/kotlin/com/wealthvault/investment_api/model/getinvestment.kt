package com.wealthvault.investment_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable




@Serializable
data class GetInvestmentResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: List<GetInvestmentData>? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class GetInvestmentData(

    @SerialName("id")
    val id: String? = null,

    @SerialName("user_id")
    val userId: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("symbol")
    val symbol: String? = null,

    @SerialName("type")
    val type: String? = null,

    @SerialName("broker_name")
    val brokerName: String? = null,

    @SerialName("quantity")
    val quantity: Double? = null,

    @SerialName("cost_per_price")
    val costPerPrice: Double? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,




    )


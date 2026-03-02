package com.example.cash_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable




@Serializable
data class GetCashResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: List<GetCashData>? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class GetCashData(

    @SerialName("id")
    val id: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("name")
    val name: String,

    @SerialName("amount")
    val ammount: Int,

    @SerialName("description")
    val description: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String,


)


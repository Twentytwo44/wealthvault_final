package com.wealthvault.cash_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable




@Serializable
data class CashIdResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: CashIdData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class CashIdData(
    @SerialName("id")
    val id: String? = null,

    @SerialName("user_id")
    val userId: String? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("amount")
    val ammount: Int? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,

)


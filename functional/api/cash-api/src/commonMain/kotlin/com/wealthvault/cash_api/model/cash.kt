package com.wealthvault.cash_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CashRequest(

    val name: String? = null,
    val ammount: Double? = null,
    val description: String? = null,
    val files:List<CashFileUploadData> = emptyList(),

    )

@Serializable
data class CashResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: CashData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class CashData(
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



@Serializable
data class CashFileUploadData(
    val bytes: ByteArray? = null,
    val mimeType: String? = null,
    val fileName: String? = null,
)

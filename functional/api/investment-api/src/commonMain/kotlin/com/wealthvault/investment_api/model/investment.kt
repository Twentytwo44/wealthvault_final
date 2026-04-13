package com.wealthvault.investment_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class InvestmentRequest(
    val name: String? = null,
    val symbol: String? = null,
    val type: String? = null,
    val brokerName: String? = null,
    val quantity: String? = null,
    val costPerPrice: String? = null,
    val description: String? = null,
    // เปลี่ยนจาก String เป็น ByteArray
    val files: List<FileUploadData> = emptyList(),
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
    val quantity: Double? = 0.0,

    @SerialName("cost_per_price")
    val costPerPrice: Double? = 0.0,

    @SerialName("description")
    val description: String? = null,

    @SerialName("files")
    val files: List<FileArray>? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,


)

@Serializable
data class FileArray(
    @SerialName("id")
    val id: String? = null,

    @SerialName("url")
    val url: String? = null,

    @SerialName("file_type")
    val fileType: String? = null,

)


@Serializable
data class FileUploadData(
    val bytes: ByteArray? = null,
    val mimeType: String? = null,
    val fileName: String? = null,
)

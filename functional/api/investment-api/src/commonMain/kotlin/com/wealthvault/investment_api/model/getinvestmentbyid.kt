package com.wealthvault.investment_api.model

import com.wealthvault.core.model.FileDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InvestmentIdResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: InvestmentIdData? = null,
    @SerialName("error") val error: String? = null
)

@Serializable
data class InvestmentIdData(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("name") val name: String? = null,
    @SerialName("symbol") val symbol: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("broker_name") val brokerName: String? = null,
    @SerialName("quantity") val quantity: Double? = null,
    @SerialName("cost_per_price") val costPerPrice: Double? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("files") val files: List<FileDataModel>? = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

// 🌟 สร้าง FileData สำหรับ Investment
//@Serializable
//data class FileDataInvestment(
//    @SerialName("id") val id: String = "",
//    @SerialName("url") override val url: String = "", // 🌟 สืบทอด HasImageUrl
//    @SerialName("file_type") override val fileType: String = ""
//) : HasImageUrl



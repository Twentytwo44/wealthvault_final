package com.wealthvault.cash_api.model

import com.wealthvault.core.model.FileDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CashIdResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: CashIdData? = null,
    @SerialName("error") val error: String? = null
)

@Serializable
data class CashIdData(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("name") val name: String? = null,
    @SerialName("amount") val amount: Double? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("files") val files: List<FileDataModel>? = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

// 🌟 สร้าง FileDataCash ให้รองรับรูปภาพ
//@Serializable
//data class FileDataCash(
//    @SerialName("id") val id: String = "",
//    @SerialName("url") override val url: String = "",
//    @SerialName("file_type") override val fileType: String = ""
//) : HasImageUrl

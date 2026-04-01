package com.wealthvault.cash_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.wealthvault.core.model.HasImageUrl // 🌟 นำเข้า Interface กลางของเรา

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
    @SerialName("name") val name: String,
    @SerialName("amount") val amount: Double, // 🌟 เปลี่ยนเป็น Double
    @SerialName("description") val description: String,

    // 🌟 เติม files เผื่อมีรูปแนบ
    @SerialName("files") val files: List<FileDataCash>? = emptyList(),

    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

// 🌟 สร้าง FileDataCash ให้รองรับรูปภาพ
@Serializable
data class FileDataCash(
    @SerialName("id") val id: String = "",
    @SerialName("url") override val url: String = "",
    @SerialName("file_type") val fileType: String = ""
) : HasImageUrl
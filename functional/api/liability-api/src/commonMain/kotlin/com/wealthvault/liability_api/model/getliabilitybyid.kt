package com.wealthvault.liability_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.wealthvault.core.model.HasImageUrl // 🌟 นำเข้า Interface รูปภาพ

@Serializable
data class LiabilityIdResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: LiabilityIdData? = null,
    @SerialName("error") val error: String? = null
)

@Serializable
data class LiabilityIdData(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("type") val type: String,
    @SerialName("name") val name: String,
    @SerialName("creditor") val creditor: String,
    @SerialName("principal") val principal: Double, // 🌟 เปลี่ยนเป็น Double เผื่อมีสตางค์
    @SerialName("interest_rate") val interestRate: Double,
    @SerialName("description") val description: String,

    // 🌟 ใส่ ? (Nullable) เผื่อบางเคสไม่มี
    @SerialName("started_at") val startedAt: String? = null,
    @SerialName("ended_at") val endedAt: String? = null,

    // 🌟 เติม files เผื่อมีรูปสัญญากู้ยืม
    @SerialName("files") val files: List<FileDataLiability>? = emptyList(),

    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

// 🌟 สร้าง FileData สำหรับ Liability
@Serializable
data class FileDataLiability(
    @SerialName("id") val id: String = "",
    @SerialName("url") override val url: String = "", // 🌟 สืบทอด HasImageUrl
    @SerialName("file_type") val fileType: String = ""
) : HasImageUrl
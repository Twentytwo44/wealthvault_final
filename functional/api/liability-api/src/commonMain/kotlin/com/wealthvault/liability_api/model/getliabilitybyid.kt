package com.wealthvault.liability_api.model

import com.wealthvault.core.model.FileDataModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    @SerialName("type") val type: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("creditor") val creditor: String? = null,
    @SerialName("principal") val principal: Double? = null,
    @SerialName("interest_rate") val interestRate: Double? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("started_at") val startedAt: String? = null,
    @SerialName("ended_at") val endedAt: String? = null,
    @SerialName("files") val files: List<FileDataModel>? = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

// 🌟 สร้าง FileData สำหรับ Liability
//@Serializable
//data class FileDataLiability(
//    @SerialName("id") val id: String = "",
//    @SerialName("url") override val url: String = "", // 🌟 สืบทอด HasImageUrl
//    @SerialName("file_type") override val fileType: String = ""
//) : HasImageUrl

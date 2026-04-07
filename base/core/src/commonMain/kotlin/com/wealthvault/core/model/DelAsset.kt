package com.wealthvault.core.model // เปลี่ยน Package ตามความเหมาะสม

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteBaseResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: DeleteBaseData? = null,
    @SerialName("error") val error: String? = null
)

@Serializable
data class DeleteBaseData(
    @SerialName("success") val success: Boolean = false // 🌟 ต้องเป็น Boolean ครับ
)
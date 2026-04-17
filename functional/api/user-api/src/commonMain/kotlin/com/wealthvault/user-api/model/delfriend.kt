package com.wealthvault.`user-api`.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteFriendResponse( // 🌟 แนะนำให้ใช้ชื่อนี้เพื่อให้สื่อความหมายครับ
    @SerialName("status")
    val status: String? = null,

    @SerialName("message")
    val message: String? = null,

    // 🌟 เปลี่ยนจาก Boolean เป็น Class wrapper ตัวใหม่ข้างล่าง
    @SerialName("data")
    val data: DeleteFriendData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class DeleteFriendData(
    @SerialName("success")
    val success: Boolean? = null
)
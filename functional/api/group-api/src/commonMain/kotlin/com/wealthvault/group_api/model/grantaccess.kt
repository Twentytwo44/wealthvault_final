package com.wealthvault.group_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class grantaccess(
    @SerialName("status")
    val status: String? = null,

    @SerialName("message")
    val message: String? = null,

    // 🌟 เปลี่ยนเป็น Boolean? เพื่อรับค่า true จาก Backend ได้ถูกต้อง
    @SerialName("data")
    val data: Boolean? = null,

    @SerialName("error")
    val error: String? = null
)
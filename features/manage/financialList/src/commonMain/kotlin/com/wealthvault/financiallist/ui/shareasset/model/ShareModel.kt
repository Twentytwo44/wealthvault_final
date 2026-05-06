package com.wealthvault.financiallist.ui.shareasset.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShareTo(
    @SerialName("email")
    val email: List<ShareInfo> = emptyList(),
    @SerialName("friend")
    val friend: List<ShareInfo> = emptyList(),
    @SerialName("group")
    val group: List<ShareInfo> = emptyList(),
)

@Serializable
data class ShareInfo(
    @SerialName("name")
    val name: String? = null,

    @SerialName("user_id") // หรือไอดีที่ใช้ส่ง API
    val userId: String = "",

    @SerialName("date")
    var date: String? = null,       // สำหรับแสดงผลภาษาไทยบน UI (เช่น 13 พ.ค. 2569)

    @SerialName("api_date")
    var apiDate: String? = null,    // สำหรับส่งค่าไป API (รูปแบบ YYYY-MM-DD)

    @SerialName("type_data")
    val typeData: String = "",      // F = Friend, G = Group, E = Email

    @SerialName("sub_text")
    val subText: String = "",       // ข้อมูล Badge (อีเมลเพื่อน หรือ จำนวนสมาชิกกลุ่ม)

    @SerialName("profile_url")
    val profileUrl: String? = null, // URL รูปภาพโปรไฟล์

    @SerialName("is_shared")
    val isShared: Boolean? = false
)
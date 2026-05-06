package com.wealthvault_final.`financial-asset`.model

import kotlinx.serialization.Serializable

@Serializable
data class ShareTo(
    val email: List<ShareInfo>,
    val friend: List<ShareInfo>,
    val group: List<ShareInfo>,
    val shareAt: String,
)

@Serializable
data class ShareInfo(
    val name: String? = null,
    val userId: String = "",
    var date: String? = null,       // วันที่โชว์บนจอ (ภาษาไทย)
    var apiDate: String? = null,    // วันที่ส่ง API (YYYY-MM-DD)
    val typeData: String = "",      // F = Friend, G = Group, E = Email
    val subText: String = "",       // ข้อมูล Badge (อีเมล หรือ จำนวนคน)
    val profileUrl: String? = null  // รูปโปรไฟล์
)

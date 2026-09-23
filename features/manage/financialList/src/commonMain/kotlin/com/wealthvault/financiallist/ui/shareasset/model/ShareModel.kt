package com.wealthvault.financiallist.ui.shareasset.model

data class ShareTo(
    val email: List<ShareInfo> = emptyList(),
    val friend: List<ShareInfo> = emptyList(),
    val group: List<ShareInfo> = emptyList(),
)

data class ShareInfo(
    val name: String? = null,
    // หรือไอดีที่ใช้ส่ง API
    val userId: String = "",
    var date: String? = null,       // สำหรับแสดงผลภาษาไทยบน UI (เช่น 13 พ.ค. 2569)
    var apiDate: String? = null,    // สำหรับส่งค่าไป API (รูปแบบ YYYY-MM-DD)
    val typeData: String = "",      // F = Friend, G = Group, E = Email
    val subText: String = "",       // ข้อมูล Badge (อีเมลเพื่อน หรือ จำนวนสมาชิกกลุ่ม)
    val profileUrl: String? = null, // URL รูปภาพโปรไฟล์
    val isShared: Boolean? = false,

    var sharedItemId: String = ""
)

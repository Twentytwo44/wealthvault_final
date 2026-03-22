package com.wealthvault_final.line_auth.model

data class LineUser(
    val userId: String,
    val displayName: String,
    val pictureUrl: String? = null,
    val accessToken: String? = null, // 👈 ใช้ยิง LINE Messaging API
    val idToken: String? = null      // 👈 ใช้ส่งให้ Backend ตรวจสอบ (JWT)
)

package com.wealthvault.`user-api`.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CloseFriendResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: List<CloseFriendData>? = null, // 🌟 รับเป็น List ตรงๆ ได้เลยตาม JSON

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class CloseFriendData(
    @SerialName("id")
    val id: String,

    @SerialName("username")
    val username: String,

    @SerialName("email")
    val email: String,

    @SerialName("first_name")
    val firstName: String,

    @SerialName("last_name")
    val lastName: String,

    @SerialName("phone_number")
    val phoneNumber: String,

    @SerialName("profile") // 🌟 ปรับให้ตรงกับ JSON key ("profile")
    val profile: String,

    @SerialName("birthday")
    val birthday: String,

    @SerialName("shared_age")
    val sharedAge: Int,

    @SerialName("share_enabled")
    val shareEnabled: Boolean,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String,

    @SerialName("is_close") // 🌟 ฟิลด์ใหม่ที่มีเพิ่มเข้ามาในเส้นนี้
    val isClose: Boolean
)
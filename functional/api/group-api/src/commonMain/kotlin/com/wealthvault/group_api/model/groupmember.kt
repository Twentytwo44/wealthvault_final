package com.wealthvault.group_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupMemberResponse(
    @SerialName("status") val status: String? = null,
    // 🌟 เปลี่ยน data มารับเป็น Wrapper object ตามโครงสร้าง JSON
    @SerialName("data") val data: GroupMemberDataWrapper? = null,
    @SerialName("error") val error: String? = null
)

// 🌟 สร้าง Wrapper มารับค่า members และ total
@Serializable
data class GroupMemberDataWrapper(
    @SerialName("members") val members: List<GroupMemberItem> = emptyList(),
    @SerialName("total") val total: Int? = null
)

// 🌟 นี่คือข้อมูลของคน (สมาชิก) จริงๆ ตามที่ JSON ส่งมา
@Serializable
data class GroupMemberItem(
    @SerialName("id") val id: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("profile") val profile: String? = null,
    @SerialName("is_friend") val isFriend: Boolean? = null
)
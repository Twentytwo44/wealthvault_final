package com.wealthvault.group_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberResponse(
    @SerialName("message")
    val message: String? = null,

    // 🌟 เปลี่ยนจาก GroupMemberData เป็น GroupMemberItem ครับ
    @SerialName("data")
    val data: List<GroupMemberItem> = emptyList(),

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class AddMemberRequest(
    @SerialName("target_id")
    val targetIds: String? = null,
)

@Serializable
data class GrantAccessRequest(
    @SerialName("target_id")
    val targetId: String? = null,

    @SerialName("item_ids")
    val itemIds: List<String>? = null
)

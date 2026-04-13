package com.wealthvault.group_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberResponse(
    @SerialName("message")
    val message: String? = null,

    @SerialName("data")
    val data: List<GroupMemberData> = emptyList(),

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
    val targetIds: String? = null,

    @SerialName("item_ids")
    val itemIds: String? = null
)

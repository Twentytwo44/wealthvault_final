package com.wealthvault.group_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GroupRequest(
//    @SerialName("id")
//    val id: String,
//
//    @SerialName("user_id")
//    val userId: String,

    @SerialName("group_name")
    val groupName: String? = null,

    @SerialName("member_ids")
    val memberIds: String? = null,

    @SerialName("profile_image")
    val profileImage: String? = null,

)

@Serializable
data class GroupResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: GroupData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class GroupData(
    @SerialName("id")
    val id: String? = null,

    @SerialName("group_name")
    val groupName: String? = null,

    @SerialName("group_profile")
    val groupProfile: String? = null,

    @SerialName("created_by")
    val createdBy: String? = null,

    @SerialName("member_count")
    val memberCount: Int? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,



)


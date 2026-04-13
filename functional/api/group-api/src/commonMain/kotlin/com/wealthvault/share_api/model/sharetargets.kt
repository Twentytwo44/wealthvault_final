package com.wealthvault.share_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ItemShareTargetsResponse(
    @SerialName("group")
    val groups: List<GroupDataList>? = null,

    @SerialName("friends")
    val friends: Boolean? = null,

    @SerialName("emails")
    val emails: List<FriendDataList>? = null,

    @SerialName("error")
    val error: List<EmailDataList>? = null
)

@Serializable
data class GroupDataList(
    @SerialName("group_id")
    val groupId: String? = null,

    @SerialName("group_name")
    val groupName: String? = null,

    @SerialName("group_image")
    val groupImage: String? = null,

    @SerialName("member_count")
    val memberCount: Int? = null,

    @SerialName("share_at")
    val shareAt: String? = null,
)

@Serializable
data class FriendDataList(
    @SerialName("firend_id")
    val friendId: String? = null,

    @SerialName("username")
    val userName: String? = null,

    @SerialName("profile_image")
    val profileImage: String? = null,

    @SerialName("share_at")
    val shareAt: String? = null,
)

@Serializable
data class EmailDataList(
    @SerialName("email")
    val email: String? = null,

    @SerialName("share_at")
    val shareAt: String? = null,

    @SerialName("is_sent")
    val isSent: Boolean? = null
)

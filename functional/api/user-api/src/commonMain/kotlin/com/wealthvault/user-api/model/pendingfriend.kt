package com.wealthvault.`user-api`.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PendingFriendResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: FriendArray? = null,

    @SerialName("error")
    val error: String? = null
)


@Serializable
data class PendingFriendArray(
    @SerialName("friend")
    val status: ArrayList<PendingFriendData>? = null
)
@Serializable
data class PendingFriendData(
    @SerialName("id")
    val id: String? = null,

    @SerialName("username")
    val username: String? = null,

    @SerialName("email")
    val email: String? = null,

    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    @SerialName("phone_number")
    val phoneNumber: String? = null,

    @SerialName("ic_nav_profile")
    val profile: String? = null,

    @SerialName("birthday")
    val birthday: String? = null,

    @SerialName("shared_age")
    val sharedAge: Int? = null,

    @SerialName("share_enabled")
    val shareEnabled: Boolean? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,

)

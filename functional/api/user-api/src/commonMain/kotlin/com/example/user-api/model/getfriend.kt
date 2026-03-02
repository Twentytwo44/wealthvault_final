package com.example.`user-api`.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FriendResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: FriendArray? = null,

    @SerialName("error")
    val error: String? = null
)


@Serializable
data class FriendArray(
    @SerialName("friend")
    val status: ArrayList<FriendData>? = null
)
@Serializable
data class FriendData(
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

    @SerialName("profile")
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
    val updatedAt: String

)

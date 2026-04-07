package com.wealthvault.`user-api`.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UpdateUserDataRequest(
    @SerialName("username") val username: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("birthday") val birthday: String,
    @SerialName("phone_number") val phoneNumber: String,
    @SerialName("profile") val profileImage: ByteArray? = null,
    @SerialName("shared_enabled") val sharedEnabled: Boolean? = null,
    @SerialName("shared_age") val sharedAge: Int? = null
)

@Serializable
data class UpdateUserDataResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: UpdateUserData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class UpdateUserData(
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

    @SerialName("profile")
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

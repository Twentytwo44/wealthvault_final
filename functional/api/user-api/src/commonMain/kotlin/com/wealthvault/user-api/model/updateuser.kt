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

    @SerialName("shared_enabled")
    val sharedEnabled: Boolean,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String,

    @SerialName("is_close")
    val isClose: Boolean? = null,

)

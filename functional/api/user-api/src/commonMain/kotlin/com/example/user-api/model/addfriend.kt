package com.example.`user-api`.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AddFriendRequest(
    @SerialName("requester_id")
    val requesterId: String,


)

@Serializable
data class AddFriendResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: AcceptFriendData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class AddFriendData(
    @SerialName("success")
    val success: String

)


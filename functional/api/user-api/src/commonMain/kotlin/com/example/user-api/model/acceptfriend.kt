package com.example.`user-api`.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AcceptFriendRequest(
    @SerialName("requester_id")
    val requesterId: String,

    @SerialName("action")
    val action: String
)

@Serializable
data class AcceptFriendResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: AcceptFriendData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
data class AcceptFriendData(
    @SerialName("success")
    val success: String

)


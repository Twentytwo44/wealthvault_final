package com.wealthvault.`user-api`.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class AcceptFriendRequest(
    @SerialName("requester_id")
    val requesterId: String,

    @SerialName("action")
    val action: String
)

@Serializable
internal data class AcceptFriendResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: AcceptFriendData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
internal data class AcceptFriendData(
    @SerialName("success")
    val success: String? = null,

)

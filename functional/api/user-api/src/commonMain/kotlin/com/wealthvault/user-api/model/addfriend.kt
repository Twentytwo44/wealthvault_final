package com.wealthvault.`user-api`.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class AddFriendRequest(
    @SerialName("requester_id")
    val requesterId: String,


)

@Serializable
internal data class AddFriendResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("data")
    val data: AcceptFriendData? = null,

    @SerialName("error")
    val error: String? = null
)

@Serializable
internal data class AddFriendData(
    @SerialName("success")
    val success: String

)

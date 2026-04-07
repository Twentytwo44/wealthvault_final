package com.wealthvault.`user-api`.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateCloseFriendResponse(
    @SerialName("data") val data: UpdateCloseFriendData?,
    @SerialName("status") val status: String?
)

@Serializable
data class UpdateCloseFriendData(
    @SerialName("success") val success: Boolean?
)
package com.wealthvault.notification_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    @SerialName("data")
    val data: List<NotificationData>? = emptyList(),

    @SerialName("status")
    val status: String? = null,

    @SerialName("error")
    val error: String? = null,

    @SerialName("success")
    val success: Boolean? = null,

    @SerialName("message")
    val message: String? = null,


    )

@Serializable
data class NotificationData(
    @SerialName("ID")
    val id: String? = null,

    @SerialName("EntityType")
    val entityType: String? = null,

    @SerialName("EntityID")
    val entityId: String? = null,

    @SerialName("Receiver")
    val reciever: String? = null,

    @SerialName("SenderID")
    val senderId: String? = null,

    @SerialName("Channel")
    val channel: String? = null,

    @SerialName("Message")
    val message: String? = null,

    @SerialName("CreatedAt")
    val createdAt: String? = null,

    @SerialName("IsRead")
    val isRead: Boolean? = null,


)

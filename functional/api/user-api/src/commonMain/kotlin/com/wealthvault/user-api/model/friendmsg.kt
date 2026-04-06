package com.wealthvault.`user-api`.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    @SerialName("messages")
    val messages: List<MessageItem>? = null
)

@Serializable
data class MessageItem(
    @SerialName("id")
    val id: String? = null,

    @SerialName("sender_id")
    val senderId: String? = null,

    @SerialName("msg_type")
    val msgType: String? = null, // เช่น "ASSET_CARD"

    @SerialName("content")
    val content: String? = null,

    @SerialName("metadata")
    val metadata: MessageMetadata? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("sender_name")
    val senderName: String? = null,

    @SerialName("sender_image")
    val senderImage: String? = null,

    @SerialName("is_me")
    val isMe: Boolean? = null // กลับมาเป็น Boolean แบบถูกต้องแล้ว!
)

@Serializable
data class MessageMetadata(
    @SerialName("asset_id")
    val assetId: String? = null,

    @SerialName("asset_type")
    val assetType: String? = null, // เช่น "investment", "account"

    @SerialName("snapshot_title")
    val snapshotTitle: String? = null
)

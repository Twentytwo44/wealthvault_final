package com.wealthvault.group_api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable




@Serializable
data class GroupMsgResponse(
    @SerialName("status")
    val status: String? = null,

    @SerialName("messages")
    val data: List<GroupMsgData> = emptyList(),

    @SerialName("error")
    val error: String? = null
)



@Serializable
data class GroupMsgData(

    @SerialName("sender_id")
    val senderId: String? = null,

    @SerialName("msg_type")
    val msgType: String? = null,

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
    val isMe: Boolean? = null,


    )

@Serializable
data class MessageMetadata(
    @SerialName("action_url")
    val assetUrl: String? = null,

    @SerialName("asset_id")
    val assetId: String? = null,

    @SerialName("asset_type")
    val assetType: String? = null, // เช่น "investment", "account"

    @SerialName("item_name")
    val itemName: String? = null,

    @SerialName("snapshot_title")
    val snapshotTitle: String? = null,

    @SerialName("is_action_required")
    val isActionRequired: Boolean? = null,

    @SerialName("is_completed")
    val isCompleted: Boolean? = null,

    @SerialName("share_at_display")
    val shareAtDisplay: String? = null,

    @SerialName("target_user_ids")
    val targetUserIds: List<String>? = null,

    @SerialName("type")
    val type: String? = null,

    @SerialName("is_deleted")
    val isDeleted: Boolean? = null
    )

    @Serializable
    data class ChatAction(
        @SerialName("action")
        val action: String,
        @SerialName("group_id")
        val groupId: String
    )

    @Serializable
    data class WsUpdateResponse(
        @SerialName("event")
        val event: String? = null,
        @SerialName("type")
        val type: String? = null,
        @SerialName("payload")
        val payload: WsPayload? = null
    )

    @Serializable
    data class WsPayload(
        @SerialName("count")
        val count: Int? = null,
        @SerialName("group_id")
        val groupId: String? = null
    )

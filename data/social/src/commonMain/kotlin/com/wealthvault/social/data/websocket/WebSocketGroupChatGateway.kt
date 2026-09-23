package com.wealthvault.social.data.websocket

import com.wealthvault.config.Config
import com.wealthvault.domain.social.GroupChatAction
import com.wealthvault.domain.social.GroupChatGateway
import com.wealthvault.domain.social.GroupChatEvent
import com.wealthvault.domain.social.GroupMessage
import com.wealthvault.domain.social.GroupMessageMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class WebSocketGroupChatGateway(
    private val service: WebSocketTransport,
    private val json: Json,
) : GroupChatGateway {
    override suspend fun connectToChat(accessToken: String?): Flow<GroupChatEvent> =
        service.connect("${Config.webSocketUrl}ws", accessToken).mapNotNull { it.toDomainEvent() }

    override suspend fun connect(url: String): Flow<GroupChatEvent> =
        service.connect(url).mapNotNull { it.toDomainEvent() }

    override suspend fun connect(url: String, accessToken: String?): Flow<GroupChatEvent> =
        service.connect(url, accessToken).mapNotNull { it.toDomainEvent() }

    override suspend fun send(action: GroupChatAction) {
        service.send(json.encodeToString(GroupChatActionWire(action.action, action.groupId)))
    }

    override suspend fun close() {
        service.close()
    }

    private fun String.toDomainEvent(): GroupChatEvent? = runCatching {
        val root = json.parseToJsonElement(this).jsonObject
        if (root["type"]?.jsonPrimitive?.content == "DATA_UPDATE") {
            root["payload"]?.let { payload ->
                json.decodeFromJsonElement(GroupSocketPayloadWire.serializer(), payload)
                    .groupId
                    ?.let(GroupChatEvent::DataUpdated)
            }
        } else {
            json.decodeFromJsonElement(GroupChatMessageWire.serializer(), root).toDomain()
        }
    }.getOrNull()
}

@Serializable
private data class GroupChatActionWire(
    val action: String,
    @SerialName("group_id") val groupId: String,
)

@Serializable
private data class GroupSocketPayloadWire(
    val count: Int? = null,
    @SerialName("group_id") val groupId: String? = null,
)

@Serializable
private data class GroupChatMessageWire(
    @SerialName("sender_id") val senderId: String? = null,
    @SerialName("msg_type") val msgType: String? = null,
    val content: String? = null,
    val metadata: GroupMessageMetadataWire? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("sender_name") val senderName: String? = null,
    @SerialName("sender_image") val senderImage: String? = null,
    @SerialName("is_me") val isMe: Boolean? = null,
) {
    fun toDomain() = GroupChatEvent.Message(
        GroupMessage(
            senderId = senderId,
            msgType = msgType,
            content = content,
            metadata = metadata?.toDomain(),
            createdAt = createdAt,
            senderName = senderName,
            senderImage = senderImage,
            isMe = isMe,
        ),
    )
}

@Serializable
private data class GroupMessageMetadataWire(
    @SerialName("action_url") val assetUrl: String? = null,
    @SerialName("asset_id") val assetId: String? = null,
    @SerialName("asset_type") val assetType: String? = null,
    @SerialName("item_name") val itemName: String? = null,
    @SerialName("snapshot_title") val snapshotTitle: String? = null,
    @SerialName("is_action_required") val isActionRequired: Boolean? = null,
    @SerialName("is_completed") val isCompleted: Boolean? = null,
    @SerialName("share_at_display") val shareAtDisplay: String? = null,
    @SerialName("target_user_ids") val targetUserIds: List<String>? = null,
    val type: String? = null,
    @SerialName("is_deleted") val isDeleted: Boolean? = null,
) {
    fun toDomain() = GroupMessageMetadata(
        assetUrl = assetUrl,
        assetId = assetId,
        assetType = assetType,
        itemName = itemName,
        snapshotTitle = snapshotTitle,
        isActionRequired = isActionRequired,
        isCompleted = isCompleted,
        shareAtDisplay = shareAtDisplay,
        targetUserIds = targetUserIds,
        type = type,
        isDeleted = isDeleted,
    )
}

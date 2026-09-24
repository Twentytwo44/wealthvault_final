package com.wealthvault.data.notification.transport.notification

import com.wealthvault.config.Config
import com.wealthvault.core.model.NotificationItem
import com.wealthvault.data.notification.transport.model.NotificationData
import com.wealthvault.data.notification.transport.model.NotificationResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class GetNotificationsApiImpl(
    private val client: HttpClient,
    private val json: Json,
) : GetNotificationsApi {
    override suspend fun getNotifications(): List<NotificationItem> {
        val response: NotificationResponse = client.get("${Config.localhost_android}notifications/").body()
        response.error?.let { error -> throw IllegalStateException(error) }
        return response.data.orEmpty().map { it.toDomain(json) }
    }
}

private fun com.wealthvault.data.notification.transport.model.NotificationData.toDomain(json: Json) = NotificationItem(
    id = id,
    entityType = entityType,
    entityId = entityId,
    receiver = reciever,
    senderId = senderId,
    channel = channel,
    message = message,
    metadata = metaData,
    createdAt = createdAt,
    isRead = isRead,
    isCompleted = parseNotificationCompleted(metaData, json),
)

internal fun parseNotificationCompleted(rawMetadata: String?, json: Json): Boolean? =
    rawMetadata?.let { raw ->
        runCatching {
            json.parseToJsonElement(raw).jsonObject["is_completed"]?.jsonPrimitive?.booleanOrNull
        }.getOrNull()
    }

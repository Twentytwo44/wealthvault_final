package com.wealthvault.notification_api.notification

import com.wealthvault.config.Config
import com.wealthvault.core.model.NotificationItem
import com.wealthvault.notification_api.model.NotificationData
import com.wealthvault.notification_api.model.NotificationResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetNotificationsApiImpl(private val client: HttpClient) : GetNotificationsApi {
    override suspend fun getNotifications(): List<NotificationItem> {
        val response: NotificationResponse = client.get("${Config.localhost_android}notifications/").body()
        response.error?.let { error -> throw IllegalStateException(error) }
        return response.data.orEmpty().map(NotificationData::toDomain)
    }
}

private fun com.wealthvault.notification_api.model.NotificationData.toDomain() = NotificationItem(
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
)

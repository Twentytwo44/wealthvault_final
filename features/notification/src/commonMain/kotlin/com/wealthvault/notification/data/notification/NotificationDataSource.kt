package com.wealthvault.notification.data.notification

import com.wealthvault.notification_api.model.NotificationData
import com.wealthvault.notification_api.notification.GetNotificationsApi


class NotificationDataSource(
    private val notificationApi: GetNotificationsApi,
) {
    suspend fun getNoti(): Result<List<NotificationData>> {
        return runCatching {
            val result = notificationApi.getNotifications()
            println("Notification Result: ${result.data}")
            result.data ?: emptyList()
        }
    }
}


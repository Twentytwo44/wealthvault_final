package com.wealthvault.notification.data.notification

import com.wealthvault.notification_api.model.NotificationResponse
import com.wealthvault.notification_api.read.PutNotiApi

class PutNotificationDataSource(
    private val putNotificationApi: PutNotiApi,
) {
    suspend fun putNoti(id:String): Result<NotificationResponse> {
        return runCatching {
            val result = putNotificationApi.putNoti(id)
            println("Notification Result: ${result.data}")
            result ?: error("Error: Put notification failed")
        }
    }
}

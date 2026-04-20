package com.wealthvault.notification.data.notification

import com.wealthvault.notification_api.model.NotificationResponse

class PutNotificationRepositoryImpl(
    private val networkDataSource: PutNotificationDataSource,
) {
    suspend fun putNoti(id:String): Result<NotificationResponse> {
        return networkDataSource.putNoti(id)
    }

}

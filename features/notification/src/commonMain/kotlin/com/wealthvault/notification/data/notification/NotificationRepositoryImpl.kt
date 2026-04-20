package com.wealthvault.notification.data.notification

import com.wealthvault.notification_api.model.NotificationData


class NotificationRepositoryImpl(
    private val networkDataSource: NotificationDataSource,
) {
    suspend fun getNoti(): Result<List<NotificationData>> {
        return networkDataSource.getNoti()
    }

}

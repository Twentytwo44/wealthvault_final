package com.wealthvault.notification_api.notification

import com.wealthvault.notification_api.model.NotificationResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetNotificationsApi {
    @GET("notifications")
    suspend fun getNotifications(): NotificationResponse
}

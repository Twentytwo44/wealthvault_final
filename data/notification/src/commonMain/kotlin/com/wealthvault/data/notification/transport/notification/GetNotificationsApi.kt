package com.wealthvault.data.notification.transport.notification

import com.wealthvault.core.model.NotificationItem

interface GetNotificationsApi {
    /** Returns domain-safe items; the wire response stays private to the adapter. */
    suspend fun getNotifications(): List<NotificationItem>
}

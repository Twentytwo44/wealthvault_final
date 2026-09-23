package com.wealthvault.core.model

import kotlinx.serialization.Serializable

/** Domain notification model; wire naming remains private to the API/data layer. */
@Serializable
data class NotificationItem(
    val id: String? = null,
    val entityType: String? = null,
    val entityId: String? = null,
    val receiver: String? = null,
    val senderId: String? = null,
    val channel: String? = null,
    val message: String? = null,
    val metadata: String? = null,
    val createdAt: String? = null,
    val isRead: Boolean? = null,
    /** Parsed notification state; the raw wire metadata remains compatibility-only. */
    val isCompleted: Boolean? = null,
)

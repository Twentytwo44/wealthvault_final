package com.wealthvault.domain.notification

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.architecture.CachedValue
import com.wealthvault.core.cache.CachedRepository
import com.wealthvault.core.model.NotificationItem
import kotlinx.coroutines.flow.Flow

/**
 * Source-compatible alias for callers that historically imported the device
 * mutation result from the notification context. The shared contract now
 * lives in core:model so auth and notification remain independent.
 */
typealias DeviceMutationResult = com.wealthvault.core.model.DeviceMutationResult

/** Stable read model for notification presentation. */
data class NotificationSnapshot(
    val value: List<NotificationItem>,
    val freshness: CacheFreshness,
)

/** Device metadata used by session and notification settings flows. */
data class DeviceInfo(
    val id: String? = null,
    val userId: String? = null,
    val token: String? = null,
    val platform: String? = null,
    val deviceName: String? = null,
    val isActive: Boolean? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

/** Domain contract; transport and persistence remain in the feature data package. */
interface NotificationRepository : CachedRepository<List<NotificationItem>> {
    suspend fun getNoti(forceRefresh: Boolean = false): AppResult<NotificationSnapshot>

    suspend fun invalidate()
}

/** Mutation boundary used by notification presentation. */
interface NotificationMutationRepository {
    suspend fun markRead(id: String): AppResult<Unit>
    suspend fun markAllRead(): AppResult<Unit>
}

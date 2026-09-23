package com.wealthvault.core.notification

import com.wealthvault.core.architecture.AppResult

/** Domain-facing notification summary; UI layers do not depend on notification DTOs. */
interface NotificationBadgeProvider {
    suspend fun hasUnread(): AppResult<Boolean>
}

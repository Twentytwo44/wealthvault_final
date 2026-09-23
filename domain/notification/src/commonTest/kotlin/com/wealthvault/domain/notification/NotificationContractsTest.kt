package com.wealthvault.domain.notification

import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.model.NotificationItem
import kotlin.test.Test
import kotlin.test.assertEquals

class NotificationContractsTest {
    @Test
    fun snapshotCarriesFreshnessAlongsideDomainItems() {
        val item = NotificationItem(
            id = "notification-1",
            entityType = "FRIEND_REQUEST",
            message = "New request",
        )

        assertEquals(
            NotificationSnapshot(listOf(item), CacheFreshness.Offline),
            NotificationSnapshot(listOf(item), CacheFreshness.Offline),
        )
    }
}

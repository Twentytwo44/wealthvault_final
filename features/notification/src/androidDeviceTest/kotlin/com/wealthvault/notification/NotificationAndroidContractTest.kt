package com.wealthvault.notification

import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.notification.viewmodel.NotificationUiAction
import com.wealthvault.notification.viewmodel.NotificationUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class NotificationAndroidContractTest {
    @Test
    fun notificationStateExposesFreshnessAndRefreshAction() {
        val state = NotificationUiState(freshness = CacheFreshness.Offline)
        assertFalse(state.isLoading)
        assertEquals(CacheFreshness.Offline, state.freshness)
        assertEquals(NotificationUiAction.Refresh, NotificationUiAction.Refresh)
    }
}

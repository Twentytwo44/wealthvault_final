package com.wealthvault.notification

import kotlin.test.Test
import com.wealthvault.notification.viewmodel.NotificationUiState
import kotlin.test.assertEquals

class NotificationStateContractTest {
    @Test
    fun defaultStateHasNoNotifications() {
        val state = NotificationUiState()

        assertEquals(emptyList(), state.items)
        assertEquals(false, state.isLoading)
        assertEquals(false, state.isRefreshing)
    }
}

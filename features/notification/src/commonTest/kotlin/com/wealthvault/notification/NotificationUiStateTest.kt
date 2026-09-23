package com.wealthvault.notification

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.notification.viewmodel.NotificationUiAction
import com.wealthvault.notification.viewmodel.NotificationUiEffect
import com.wealthvault.notification.viewmodel.NotificationUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class NotificationUiStateTest {
    @Test
    fun stateAndUdfContractsExposeRefreshAndError() {
        val state = NotificationUiState(freshness = CacheFreshness.Stale, isRefreshing = true)
        assertEquals(CacheFreshness.Stale, state.freshness)
        assertEquals(true, state.isRefreshing)
        assertIs<NotificationUiAction.Refresh>(NotificationUiAction.Refresh)
        assertIs<NotificationUiEffect.ShowError>(NotificationUiEffect.ShowError(AppError.NotFound))
    }
}

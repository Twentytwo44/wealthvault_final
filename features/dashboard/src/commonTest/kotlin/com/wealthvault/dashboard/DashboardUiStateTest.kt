package com.wealthvault.dashboard

import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.dashboard.ui.DashboardUiState
import kotlin.test.Test
import kotlin.test.assertEquals

class DashboardUiStateTest {
    @Test
    fun stateCarriesUnreadAndFreshnessMetadata() {
        val state = DashboardUiState(hasUnreadNotifications = true, freshness = CacheFreshness.Stale)
        assertEquals(true, state.hasUnreadNotifications)
        assertEquals(CacheFreshness.Stale, state.freshness)
    }
}

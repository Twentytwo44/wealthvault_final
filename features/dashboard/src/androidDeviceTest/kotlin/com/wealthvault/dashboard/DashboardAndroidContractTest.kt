package com.wealthvault.dashboard

import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.dashboard.ui.DashboardUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DashboardAndroidContractTest {
    @Test
    fun dashboardStartsWithoutLoadingAndTracksCacheFreshness() {
        val state = DashboardUiState(freshness = CacheFreshness.Stale)
        assertFalse(state.isLoading)
        assertEquals(CacheFreshness.Stale, state.freshness)
    }
}

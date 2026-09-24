package com.wealthvault.dashboard

import kotlin.test.Test
import com.wealthvault.core.architecture.CacheFreshness
import kotlin.test.assertEquals

class DashboardStateContractTest {
    @Test
    fun defaultStateIsEmptyAndFresh() {
        val state = com.wealthvault.dashboard.ui.DashboardUiState()

        assertEquals(null, state.data)
        assertEquals(false, state.isLoading)
        assertEquals(CacheFreshness.Fresh, state.freshness)
    }
}

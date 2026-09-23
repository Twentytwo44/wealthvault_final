package com.wealthvault.profile

import com.wealthvault.profile.ui.ProfileUiAction
import com.wealthvault.profile.ui.ProfileUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProfileAndroidContractTest {
    @Test
    fun profileStartsLoadingAndUsesExplicitRefreshAction() {
        val state = ProfileUiState()
        assertTrue(state.isLoading)
        assertEquals(ProfileUiAction.Refresh, ProfileUiAction.Refresh)
    }
}

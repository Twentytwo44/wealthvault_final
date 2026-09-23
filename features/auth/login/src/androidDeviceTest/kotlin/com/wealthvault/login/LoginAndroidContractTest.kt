package com.wealthvault.login

import com.wealthvault.login.ui.LoginUiAction
import com.wealthvault.login.ui.LoginUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class LoginAndroidContractTest {
    @Test
    fun loginStartsWithAnIdleUdfStateAndTypedValidationAction() {
        val state = LoginUiState()
        assertFalse(state.isLoading)
        assertEquals(null, state.errorMessage)
        assertEquals("invalid", LoginUiAction.ValidationFailed("invalid").message)
    }
}

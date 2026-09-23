package com.wealthvault.register

import com.wealthvault.register.ui.RegisterUiAction
import com.wealthvault.register.ui.RegisterUiState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class RegisterAndroidContractTest {
    @Test
    fun registrationStateKeepsPasswordFieldsIndependent() {
        val state = RegisterUiState(password = "a", confirmPassword = "b")
        assertFalse(state.isLoading)
        assertEquals("a", state.password)
        assertEquals("b", state.confirmPassword)
        assertEquals("user", RegisterUiAction.UsernameChanged("user").value)
    }
}

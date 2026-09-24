package com.wealthvault.register

import kotlin.test.Test
import com.wealthvault.domain.auth.RegistrationCredentials
import com.wealthvault.register.ui.RegisterUiAction
import com.wealthvault.register.ui.RegisterUiEffect
import com.wealthvault.register.ui.RegisterUiState
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RegistrationContractTest {
    @Test
    fun credentialsAreRepresentedWithoutApiDto() {
        val credentials = RegistrationCredentials(username = "new-user", password = "secret")

        assertEquals("new-user", credentials.username)
        assertEquals("secret", credentials.password)
    }

    @Test
    fun uiContractKeepsRegistrationStateLocalToTheFeature() {
        val state = RegisterUiState(username = "user", password = "p", confirmPassword = "p")

        assertEquals("p", state.confirmPassword)
        assertEquals("user", RegisterUiAction.UsernameChanged("user").value)
        assertIs<RegisterUiAction.Submit>(RegisterUiAction.Submit)
        assertIs<RegisterUiEffect.Registered>(RegisterUiEffect.Registered)
    }
}

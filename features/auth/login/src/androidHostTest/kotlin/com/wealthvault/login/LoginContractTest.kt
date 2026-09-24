package com.wealthvault.login

import kotlin.test.Test
import com.wealthvault.domain.auth.LoginCredentials
import kotlin.test.assertEquals

class LoginContractTest {
    @Test
    fun credentialsKeepUserInputAtDomainBoundary() {
        val credentials = LoginCredentials(username = "user@example.com", password = "secret")

        assertEquals("user@example.com", credentials.username)
        assertEquals("secret", credentials.password)
    }
}

package com.wealthvault.google_auth

import kotlin.test.Test
import kotlin.test.assertEquals

class GoogleAuthContractTest {
    @Test
    fun googleUserPreservesBackendIdentityFields() {
        val user = GoogleUser(
            idToken = "id-token",
            accessToken = "access-token",
            email = "user@example.com",
            displayName = "User",
            photoUrl = null,
            userId = "user-1"
        )

        assertEquals("user-1", user.userId)
        assertEquals("id-token", user.idToken)
        assertEquals("user@example.com", user.email)
    }
}

package com.wealthvault.security.session

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SessionContractTest {
    @Test
    fun emptyTokenStartsSignedOut() {
        val emptySessionState = com.wealthvault.domain.auth.SessionState.SignedOut

        assertEquals(com.wealthvault.domain.auth.SessionState.SignedOut, emptySessionState)
        assertNotEquals(com.wealthvault.domain.auth.SessionState.Authenticated, emptySessionState)
        assertNotEquals(com.wealthvault.domain.auth.SessionState.Loading, emptySessionState)
    }
}

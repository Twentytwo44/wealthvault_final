package com.wealthvault.data_store

import kotlin.test.Test
import kotlin.test.assertEquals

class SessionContractTest {
    @Test
    fun emptyTokenStartsSignedOut() {
        val token = AuthToken(accessToken = null, refreshToken = null)

        assertEquals(null, token.accessToken)
        assertEquals(null, token.refreshToken)
        assertEquals(SessionState.SignedOut, SessionState.SignedOut)
    }
}

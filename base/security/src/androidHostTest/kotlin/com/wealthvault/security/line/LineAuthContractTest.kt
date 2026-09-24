package com.wealthvault.security.line

import kotlin.test.Test
import com.wealthvault.security.line.model.LineUser
import kotlin.test.assertEquals

class LineAuthContractTest {
    @Test
    fun lineUserKeepsOptionalTokensOptional() {
        val user = LineUser(userId = "line-1", displayName = "User")

        assertEquals("line-1", user.userId)
        assertEquals("User", user.displayName)
        assertEquals(null, user.accessToken)
        assertEquals(null, user.idToken)
    }
}

package com.wealthvault.data.auth.google

import com.wealthvault.domain.auth.GoogleIdentity
import kotlin.test.Test
import kotlin.test.assertEquals

class GoogleAuthContractTest {
    @Test
    fun googleProviderExposesOnlyTheDomainIdentity() {
        val user = GoogleIdentity(idToken = "id-token")

        assertEquals("id-token", user.idToken)
    }
}

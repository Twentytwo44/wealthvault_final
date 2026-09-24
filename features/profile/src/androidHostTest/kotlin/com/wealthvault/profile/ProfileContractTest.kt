package com.wealthvault.profile

import kotlin.test.Test
import com.wealthvault.domain.profile.UserData
import kotlin.test.assertEquals

class ProfileContractTest {
    @Test
    fun userDataUsesSafeDefaults() {
        val user = UserData()

        assertEquals(null, user.id)
        assertEquals(false, user.shareEnabled)
    }
}

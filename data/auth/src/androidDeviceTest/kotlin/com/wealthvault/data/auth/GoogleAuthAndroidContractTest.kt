package com.wealthvault.data.auth.google

import androidx.test.platform.app.InstrumentationRegistry
import kotlin.test.Test
import kotlin.test.assertIs

class GoogleAuthAndroidContractTest {
    @Test
    fun credentialFactoryCreatesTheDomainProviderWithoutLaunchingUi() {
        val provider = GoogleAuthFactory(
            InstrumentationRegistry.getInstrumentation().targetContext,
        ).create()
        assertIs<GoogleAuthAndroid>(provider)
    }
}

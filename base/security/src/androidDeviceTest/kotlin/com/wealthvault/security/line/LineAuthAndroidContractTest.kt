package com.wealthvault.security.line

import androidx.test.platform.app.InstrumentationRegistry
import kotlin.test.Test
import kotlin.test.assertIs

class LineAuthAndroidContractTest {
    @Test
    fun platformAdapterCanBeConstructedWithoutStartingInteractiveLogin() {
        val adapter = LineAuthAndroid(
            context = InstrumentationRegistry.getInstrumentation().targetContext,
            launchIntent = {},
        )
        assertIs<LineAuthAndroid>(adapter)
    }
}

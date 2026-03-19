<<<<<<<< HEAD:functional/api/line-auth/src/androidDeviceTest/kotlin/com/wealthvault_final/line_auth/ExampleInstrumentedTest.kt
package com.wealthvault_final.line_auth
========
package com.wealthvault.financial_obligations
>>>>>>>> main:features/manage/financial-obligations/src/androidDeviceTest/kotlin/com/wealthvault/financial_obligations/ExampleInstrumentedTest.kt

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.wealthvault_final.line_auth.test", appContext.packageName)
    }
}

package com.wealthvault.security.session

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidSecureStorageTest {
    @Test
    fun keystoreBackedStorageRoundTripsAndRemovesASecret() = runBlocking {
        val storage = AndroidSecureStorage(
            InstrumentationRegistry.getInstrumentation().targetContext,
        )
        val key = "instrumentation-${System.currentTimeMillis()}"
        storage.write(key, "secret-value")
        assertEquals("secret-value", storage.read(key))
        storage.remove(key)
        assertEquals(null, storage.read(key))
    }
}

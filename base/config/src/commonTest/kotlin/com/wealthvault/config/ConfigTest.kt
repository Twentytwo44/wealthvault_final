package com.wealthvault.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConfigTest {
    @Test
    fun productionEndpointsUseEncryptedTransport() {
        Config.requireSecureTransport()

        assertTrue(Config.apiBaseUrl.startsWith("https://"))
        assertTrue(Config.webSocketUrl.startsWith("wss://"))
        assertEquals(Config.apiBaseUrl, Config.localhost_android)
    }
}

package com.wealthvault.navigation

import kotlin.test.Test
import com.wealthvault.core.navigation.SharedScreen
import kotlin.test.assertEquals

class NavigationContractTest {
    @Test
    fun mainDestinationIsTypedAndScreenBacked() {
        assertEquals("MainScreen", MainScreen()::class.simpleName)
        assertEquals("Main", SharedScreen.Main::class.simpleName)
    }
}

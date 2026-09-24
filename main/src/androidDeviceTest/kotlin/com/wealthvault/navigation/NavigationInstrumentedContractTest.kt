package com.wealthvault.navigation

import cafe.adriel.voyager.core.screen.Screen
import com.wealthvault.core.navigation.SharedScreen
import kotlin.test.Test
import kotlin.test.assertEquals

/** Device-side smoke test for the navigation contract used by the app root. */
class NavigationInstrumentedContractTest {
    @Test
    fun mainDestinationRemainsTypedAndScreenBacked() {
        val mainScreen: Screen = MainScreen()

        assertEquals(MainScreen::class, mainScreen::class)
        assertEquals("Main", SharedScreen.Main::class.simpleName)
        assertEquals(false, SharedScreen.Main::class == SharedScreen.Login::class)
    }
}

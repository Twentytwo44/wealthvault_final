package com.wealthvault.financial_obligations.di

import cafe.adriel.voyager.core.registry.screenModule
import com.wealthvault.core.navigation.SharedScreen
import com.wealthvault.`financial-obligations`.ui.menu.ObMenuScreen

/** Composition-root registration for the retained compatibility debt menu. */
val debtMenuScreenModule = screenModule {
    register<SharedScreen.DebtMenu> { ObMenuScreen() }
}

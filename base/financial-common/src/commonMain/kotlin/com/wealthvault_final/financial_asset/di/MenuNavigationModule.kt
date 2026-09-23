package com.wealthvault.financial_asset.di

import cafe.adriel.voyager.core.registry.screenModule
import com.wealthvault.core.navigation.SharedScreen
import com.wealthvault.`financial-asset`.ui.menu.MenuScreen

/** Typed navigation contract for the financial-entry menu. */
val financialMenuScreenModule = screenModule {
    register<SharedScreen.FinancialMenu> { MenuScreen() }
}

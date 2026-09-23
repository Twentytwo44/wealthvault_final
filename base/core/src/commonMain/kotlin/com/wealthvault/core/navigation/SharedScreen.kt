package com.wealthvault.core.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider

/**
 * Typed navigation destinations shared by features and the composition root.
 *
 * Keeping destination contracts in core prevents feature modules from
 * depending on the app/navigation implementation module. The actual screen
 * registrations remain in the owning feature or composition module.
 */
sealed class SharedScreen : ScreenProvider {
    data object Login : SharedScreen()
    data object Register : SharedScreen()
    data object Main : SharedScreen()
    data object DashboardTab : SharedScreen()
    data object ProfileTab : SharedScreen()
    data object AssetTab : SharedScreen()
    data object DebtTab : SharedScreen()
    data object SocialTab : SharedScreen()
    data object Notification : SharedScreen()
    data object FinancialMenu : SharedScreen()
    data object CreateCash : SharedScreen()
    data object CreateBankAccount : SharedScreen()
    data object CreateInvestment : SharedScreen()
    data object CreateInsurance : SharedScreen()
    data object CreateBuilding : SharedScreen()
    data object CreateLand : SharedScreen()
    data object CreateRealEstate : SharedScreen()
    data object CreateLiability : SharedScreen()
    data object CreateExpense : SharedScreen()
    /** Entry point for adding a liability or recurring expense. */
    data object DebtMenu : SharedScreen()
    data object AddFriend : SharedScreen()
}

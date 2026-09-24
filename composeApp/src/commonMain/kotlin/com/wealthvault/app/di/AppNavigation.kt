package com.wealthvault.app.di

import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.screenModule
import com.wealthvault.core.navigation.SharedScreen
import com.wealthvault.dashboard.tab.dashboardTabModule
import com.wealthvault.financiallist.tab.assetTabModule
import com.wealthvault.financiallist.tab.debtTabModule
import com.wealthvault.login.ui.loginScreenModule
import com.wealthvault.navigation.mainScreenModule
import com.wealthvault.notification.di.notificationScreenModule
import com.wealthvault.profile.tab.profileTabModule
import com.wealthvault.register.di.registerScreenModule
import com.wealthvault.social.tab.socialTabModule
import com.wealthvault.financiallist.di.financialListCreateMenuScreenModule

/** Registers every typed navigation destination from the composition root. */
fun registerAppNavigation() {
    ScreenRegistry {
        loginScreenModule()
        registerScreenModule()
        mainScreenModule()
        profileTabModule()
        dashboardTabModule()
        assetTabModule()
        debtTabModule()
        socialTabModule()
        notificationScreenModule()
        financialListCreateMenuScreenModule()
    }
}

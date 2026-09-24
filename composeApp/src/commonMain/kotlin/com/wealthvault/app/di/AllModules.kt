package com.wealthvault.app.di

import com.wealthvault.core.di.coreModule
import com.wealthvault.database.databaseModule
import com.wealthvault.security.SecuritySessionModule
import com.wealthvault.data.auth.AuthDataModule
import com.wealthvault.data.dashboard.DashboardDataModule
import com.wealthvault.data.profile.ProfileDataModule
import com.wealthvault.data.notification.NotificationDataModule
import com.wealthvault.data.portfolio.PortfolioDataModule
import com.wealthvault.data.social.SocialDataModule
import com.wealthvault.di.dashboardModule
import com.wealthvault.introduction.di.IntroModule
import com.wealthvault.login.di.LoginModule
import com.wealthvault.forgetpassword.di.ForgetModule
import com.wealthvault.notification.di.NotificationModule as FeatureNotificationModule
import com.wealthvault.push.di.pushNotificationModule
import com.wealthvault.profile.di.ProfileModule
import com.wealthvault.financiallist.di.financiallistModule
import com.wealthvault.register.di.RegisterModule
import com.wealthvault.social.di.SocialModule
import com.wealthvault.network.NetworkDataModule
import com.wealthvault.app.navigation.AppCoordinator
import com.wealthvault.app.navigation.SessionDeviceRegistrar
import org.koin.core.module.Module
import org.koin.dsl.module


object AllModules {
    val modules = arrayListOf<Module>().apply {

        add(coreModule)
        add(
            module {
                single { SessionDeviceRegistrar(get(), get(), get(), get()) }
                single { AppCoordinator(get(), get(), get(), get()) }
            },
        )
        add(databaseModule)
        addAll(SecuritySessionModule.allModules)
        // Bounded-context facades own legacy transport wiring during the
        // staged migration. The app composition root never imports endpoint
        // implementations directly.
        addAll(NetworkDataModule.allModules)
        addAll(AuthDataModule.allModules)
        addAll(DashboardDataModule.allModules)
        addAll(ProfileDataModule.allModules)
        addAll(PortfolioDataModule.allModules)
        addAll(SocialDataModule.allModules)

        add(LoginModule.allModules)
        add(ForgetModule.allModules)
        add(RegisterModule.allModules)
        add(dashboardModule)
        add(ProfileModule.allModules)
        // Shared financial-list graph is part of the composition root on both
        // Android and iOS; platform launchers must not assemble it separately.
        add(financiallistModule)
        add(SocialModule.allModules)
        addAll(NotificationDataModule.allModules)
        add(FeatureNotificationModule.allModules)
        add(pushNotificationModule)
        add(IntroModule.allModules)





    }
}
//
//object AllModules {
//    val modules = arrayListOf<Module>().apply {
////        add(NavigationModule.allModules)
////        add(ApiModule.allModules)
////        add(DataStoreModule.allModules)
////        add(HomeModule.allModules)
//    }
//}

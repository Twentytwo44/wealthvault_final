package com.wealthvault_final.notification.di

import com.wealthvault_final.notification.AndroidPushNotificationHelper
import com.wealthvault_final.notification.PushNotificationHelper
import org.koin.dsl.module


object NotificationModule {
    val allModules = module {
        single<PushNotificationHelper> { AndroidPushNotificationHelper() }
    }
}

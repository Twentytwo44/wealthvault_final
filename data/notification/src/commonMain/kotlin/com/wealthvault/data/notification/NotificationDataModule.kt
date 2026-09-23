package com.wealthvault.data.notification

import com.wealthvault.data.notification.transport.di.NotificationApiModule
import org.koin.core.module.Module

/**
 * Compatibility facade for notification transport while the module is moved
 * from functional/notification into the data bounded-context layout.
 */
object NotificationDataModule {
    val allModules: List<Module> = listOf(
        NotificationApiModule.allModules,
        notificationRepositoriesModule,
    )
}

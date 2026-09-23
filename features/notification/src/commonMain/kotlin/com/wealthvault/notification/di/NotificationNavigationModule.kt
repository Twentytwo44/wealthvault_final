package com.wealthvault.notification.di

import cafe.adriel.voyager.core.registry.screenModule
import com.wealthvault.core.navigation.SharedScreen
import com.wealthvault.notification.ui.NotificationScreen

/** Voyager registrations live at the composition root, not in another feature. */
val notificationScreenModule = screenModule {
    register<SharedScreen.Notification> { NotificationScreen() }
}

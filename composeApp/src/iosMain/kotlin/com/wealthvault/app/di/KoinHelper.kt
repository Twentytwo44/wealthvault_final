package com.wealthvault.app.di

import com.wealthvault.app.navigation.SessionDeviceRegistrar
import com.wealthvault.database.iosDatabaseModule
import com.wealthvault.data.auth.AuthIosDataModule
import com.wealthvault.domain.auth.PushDeviceToken
import com.wealthvault.security.iosSecurityStorageModule
import com.wealthvault.security.line.SwiftLineAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.UIKit.UIViewController

private val iosHostScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
private var iosDeviceRegistrar: SessionDeviceRegistrar? = null

fun initKoin(controller: UIViewController, swiftLineAuth: SwiftLineAuth) {
    val application = startKoin {
        modules(
            AllModules.modules +
                iosSecurityStorageModule +
                iosDatabaseModule +
                AuthIosDataModule.allModules +
                module {
                    single { controller }
                    single<SwiftLineAuth> { swiftLineAuth }
                },
        )
    }
    iosDeviceRegistrar = application.koin.get()
}

/**
 * Narrow host bridge used by Firebase Messaging when an iOS FCM token rotates.
 * If the callback arrives before Compose/Koin starts, AppDelegate has already
 * persisted the token and the authenticated startup registrar will retry it.
 */
fun notifyIosPushTokenChanged(token: String) {
    val normalizedToken = token.trim()
    if (normalizedToken.isEmpty()) return

    val registrar = iosDeviceRegistrar ?: return

    iosHostScope.launch {
        registrar.onDeviceTokenChanged(
            PushDeviceToken(
                fcmToken = normalizedToken,
                platform = "iOS",
                deviceName = "iOS device",
            ),
        )
    }
}

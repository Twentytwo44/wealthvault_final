package com.wealthvault.push.di

import com.wealthvault.push.PushNotificationHelper
import com.wealthvault.push.model.DeviceTokenInfo
import com.wealthvault.domain.auth.PushNotificationProvider
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

private class IosPushNotificationHelper : PushNotificationHelper {
    override fun getDeviceTokenInfo(
        onSuccess: (DeviceTokenInfo) -> Unit,
        onError: (String) -> Unit,
    ) {
        // APNs/Firebase registration is owned by the iOS host application.
        // AppDelegate writes the current FCM token to this narrow host bridge;
        // the feature never imports Firebase or UIKit implementation types.
        val token = NSUserDefaults.standardUserDefaults
            .stringForKey("wealthvault.push.fcm_token")
            ?.takeIf { it.isNotBlank() }
        if (token == null) {
            onError("iOS push notification token is not available")
            return
        }
        onSuccess(
            DeviceTokenInfo(
                fcmToken = token,
                platform = "iOS",
                deviceName = "iOS device",
            ),
        )
    }
}

actual val pushNotificationModule: Module = module {
    single<PushNotificationHelper> { IosPushNotificationHelper() }
    single<PushNotificationProvider> { get<PushNotificationHelper>() }
}

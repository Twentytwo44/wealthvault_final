package com.wealthvault.push.di

import com.wealthvault.push.AndroidPushNotificationHelper
import com.wealthvault.push.PushNotificationHelper
import com.wealthvault.domain.auth.PushNotificationProvider
import org.koin.core.module.Module
import org.koin.dsl.module

actual val pushNotificationModule: Module = module {
    single<PushNotificationHelper> { AndroidPushNotificationHelper(get()) }
    single<PushNotificationProvider> { get<PushNotificationHelper>() }
}

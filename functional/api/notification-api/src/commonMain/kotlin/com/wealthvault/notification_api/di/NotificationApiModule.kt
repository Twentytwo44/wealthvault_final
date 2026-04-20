package com.wealthvault.notification_api.di

import com.wealthvault.core.KoinConst

import com.wealthvault.notification_api.getalldevice.GetAllDeviceApi
import com.wealthvault.notification_api.getalldevice.GetAllDeviceApiImpl
import com.wealthvault.notification_api.notification.GetNotificationsApi
import com.wealthvault.notification_api.notification.GetNotificationsApiImpl
import com.wealthvault.notification_api.read.PutNotiApi
import com.wealthvault.notification_api.read.PutNotiApiImpl
import com.wealthvault.notification_api.registerdevice.AddDevicesApi
import com.wealthvault.notification_api.registerdevice.AddDevicesApiImpl
import com.wealthvault.notification_api.unregisterdevice.UnDevicesApi
import com.wealthvault.notification_api.unregisterdevice.UnDevicesApiImpl
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

object NotificationApiModule {
    val allModules = module {
        single<Json>(named(KoinConst.KotlinSerialization.GLOBAL)) {
            Json {
                ignoreUnknownKeys = true
            }
        }

        single<AddDevicesApi> { AddDevicesApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<PutNotiApi> { PutNotiApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<GetNotificationsApi> { GetNotificationsApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<UnDevicesApi> { UnDevicesApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<GetAllDeviceApi> { GetAllDeviceApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }


    }


}

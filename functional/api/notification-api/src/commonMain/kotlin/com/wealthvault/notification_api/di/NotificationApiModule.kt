package com.wealthvault.notification_api.di

import com.wealthvault.core.KoinConst

import com.wealthvault.notification_api.getalldevice.GetAllDeviceApi
import com.wealthvault.notification_api.getalldevice.GetAllDeviceApiImpl
import com.wealthvault.notification_api.notification.GetNotificationsApi
import com.wealthvault.notification_api.notification.GetNotificationsApiImpl
import com.wealthvault.notification_api.read.PutNotiApi
import com.wealthvault.notification_api.read.PutNotiApiImpl
import com.wealthvault.notification_api.readall.PutNotiReadAllApi
import com.wealthvault.notification_api.readall.PutNotiReadAllApiImpl
import com.wealthvault.notification_api.registerdevice.AddDevicesApi
import com.wealthvault.notification_api.registerdevice.AddDevicesApiImpl
import com.wealthvault.notification_api.unregisterdevice.UnDevicesApi
import com.wealthvault.notification_api.unregisterdevice.UnDevicesApiImpl
import io.ktor.client.HttpClient
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

        single<AddDevicesApi> { AddDevicesApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<PutNotiApi> { PutNotiApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetNotificationsApi> { GetNotificationsApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UnDevicesApi> { UnDevicesApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetAllDeviceApi> { GetAllDeviceApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<PutNotiReadAllApi> { PutNotiReadAllApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }

    }


}

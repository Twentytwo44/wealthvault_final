package com.wealthvault.data.notification.transport.di

import com.wealthvault.core.KoinConst

import com.wealthvault.data.notification.transport.getalldevice.GetAllDeviceApi
import com.wealthvault.data.notification.transport.getalldevice.GetAllDeviceApiImpl
import com.wealthvault.data.notification.transport.notification.GetNotificationsApi
import com.wealthvault.data.notification.transport.notification.GetNotificationsApiImpl
import com.wealthvault.data.notification.transport.read.PutNotiApi
import com.wealthvault.data.notification.transport.read.PutNotiApiImpl
import com.wealthvault.data.notification.transport.readall.PutNotiReadAllApi
import com.wealthvault.data.notification.transport.readall.PutNotiReadAllApiImpl
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

object NotificationApiModule {
    val allModules = module {
        single<PutNotiApi> { PutNotiApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetNotificationsApi> {
            GetNotificationsApiImpl(
                client = get<HttpClient>(named(KoinConst.HttpClient.GLOBAL)),
                json = get(named(KoinConst.KotlinSerialization.GLOBAL)),
            )
        }
        single<GetAllDeviceApi> { GetAllDeviceApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<PutNotiReadAllApi> { PutNotiReadAllApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }

    }


}

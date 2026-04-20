package com.wealthvault.notification.di

import com.wealthvault.notification.data.friend.AcceptFriendDataSource
import com.wealthvault.notification.data.friend.AcceptFriendRepositoryImpl
import com.wealthvault.notification.data.notification.NotificationDataSource
import com.wealthvault.notification.data.notification.NotificationRepositoryImpl
import com.wealthvault.notification.data.notification.PutNotificationDataSource
import com.wealthvault.notification.data.notification.PutNotificationRepositoryImpl
import com.wealthvault.notification.usecase.NotificationUseCase
import com.wealthvault.notification.viewmodel.NotificationScreenModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

object NotificationModule {
    val allModules = module {

        single { Dispatchers.IO }

        factory{ NotificationUseCase(get(),get() ) }
        factory{ NotificationScreenModel(get(),get(),get() )}

        // notfication api
        factory { NotificationDataSource(get()) }
        single<NotificationRepositoryImpl> {
            NotificationRepositoryImpl(get())
        }
        factory { PutNotificationDataSource(get()) }
        single<PutNotificationRepositoryImpl>
        {
            PutNotificationRepositoryImpl(get())
        }

        factory { AcceptFriendDataSource(get()) }
        single<AcceptFriendRepositoryImpl> {
            AcceptFriendRepositoryImpl(get())
        }


    }
}

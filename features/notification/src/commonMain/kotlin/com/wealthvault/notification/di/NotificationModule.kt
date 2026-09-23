package com.wealthvault.notification.di

import com.wealthvault.notification.usecase.NotificationUseCase
import com.wealthvault.notification.viewmodel.NotificationScreenModel
import org.koin.dsl.module

object NotificationModule {
    val allModules = module {

        factory { NotificationUseCase(get(), get(), get()) }
        factory { NotificationScreenModel(get(), get(), get()) }
    }
}

package com.wealthvault.splashscreen.di

import com.wealthvault.splashscreen.SplashScreenModel
import com.wealthvault.splashscreen.data.UserDataSource
import com.wealthvault.splashscreen.data.UserRepositoryImpl
import org.koin.dsl.module

object GetUserModule {
    val allModules = module {
            factory { SplashScreenModel(get(),get()) }
            factory { UserDataSource(get()) }
            single<UserRepositoryImpl> {
                UserRepositoryImpl(get())
            }
    }
}

package com.wealthvault.splashscreen.di

import com.wealthvault.splashscreen.SplashScreenModel
import org.koin.dsl.module

object GetUserModule {
    val allModules = module {
            factory { SplashScreenModel(get(), get()) }
    }
}

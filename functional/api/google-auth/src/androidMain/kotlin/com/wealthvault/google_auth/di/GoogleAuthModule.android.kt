package com.wealthvault.google_auth.di

import com.wealthvault.google_auth.GoogleAuthFactory
import org.koin.dsl.module

object GoogleAuthAndroidModule {
    val allModules = module {
        single { GoogleAuthFactory(get()) }

    }
}

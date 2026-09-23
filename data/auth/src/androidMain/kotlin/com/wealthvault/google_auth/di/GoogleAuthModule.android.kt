package com.wealthvault.data.auth.google.di

import com.wealthvault.data.auth.google.GoogleAuthFactory
import org.koin.dsl.module

object GoogleAuthAndroidModule {
    val allModules = module {
        single { GoogleAuthFactory(get()) }

    }
}

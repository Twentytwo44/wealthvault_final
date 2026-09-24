package com.wealthvault.data.auth.google.di

import com.wealthvault.data.auth.google.GoogleAuthFactory
import com.wealthvault.data.auth.google.GoogleAuthRepository
import org.koin.dsl.module

object GoogleAuthMainModule {
    val allModules = module {
        single { get<GoogleAuthFactory>().create() }
        single { GoogleAuthRepository(get()) }
    }
}

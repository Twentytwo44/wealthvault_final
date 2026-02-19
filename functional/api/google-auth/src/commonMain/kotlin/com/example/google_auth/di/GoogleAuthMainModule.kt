package com.example.google_auth.di

import com.example.google_auth.GoogleAuthFactory
import com.example.google_auth.GoogleAuthRepository
import org.koin.dsl.module

object GoogleAuthMainModule {
    val allModules = module {
        single { get<GoogleAuthFactory>().create() }
        single { GoogleAuthRepository(get()) }
    }
}

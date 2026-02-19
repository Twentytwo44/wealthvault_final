package com.example.google_auth.di

import com.example.google_auth.GoogleAuthFactory
import org.koin.dsl.module

object GoogleAuthIOSModule {
    val allModules = module {
        single { GoogleAuthFactory(get()) }

    }
}

package com.example.wealthvault_final.di

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(AllModules.modules)
    }
}
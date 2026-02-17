package com.example.wealthvault_final.di


import com.wealthvault.data_store.iosDataStoreModule
import com.wealthvault.wealthvault_final.di.AllModules
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            AllModules.modules + iosDataStoreModule.allModules
        )
    }
}

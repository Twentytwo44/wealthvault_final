package com.wealthvault.data_store.di

import com.wealthvault.data_store.TokenStore
import org.koin.dsl.module


object DataStoreModule {
    val allModules = module {
//        single<DataStore<Preferences>> {
//            createDataStore()
//        }

        single {
            TokenStore(get())
        }
    }
}

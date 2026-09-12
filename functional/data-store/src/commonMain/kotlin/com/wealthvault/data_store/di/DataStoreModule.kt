package com.wealthvault.data_store.di

import com.wealthvault.data_store.TokenStore
import com.wealthvault.data_store.SessionStore
import org.koin.dsl.module


object DataStoreModule {
    val allModules = module {
//        single<DataStore<Preferences>> {
//            createDataStore()
//        }

        single {
            TokenStore(get())
        }

        single<SessionStore> { get<TokenStore>() }
    }
}

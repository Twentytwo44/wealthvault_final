package com.wealthvault.data_store.di

import com.wealthvault.data_store.TokenStore
import com.wealthvault.data_store.SessionStore
import com.wealthvault.data_store.SessionManager
import com.wealthvault.domain.auth.SessionTokenStore
import org.koin.dsl.module


object DataStoreModule {
    val allModules = module {
//        single<DataStore<Preferences>> {
//            createDataStore()
//        }

        single {
            TokenStore(get(), get())
        }

        single<SessionStore> { get<TokenStore>() }
        single<SessionManager> { get<TokenStore>() }
        single<com.wealthvault.domain.auth.SessionManager> { get<TokenStore>() }
        single<SessionTokenStore> { get<TokenStore>() }
    }
}

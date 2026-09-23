package com.wealthvault.security.session.di

import com.wealthvault.security.session.TokenStore
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

        single<com.wealthvault.domain.auth.SessionManager> { get<TokenStore>() }
        single<SessionTokenStore> { get<TokenStore>() }
    }
}

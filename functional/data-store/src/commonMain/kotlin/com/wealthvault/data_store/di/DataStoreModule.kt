package com.wealthvault.data_store.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.wealthvault.core.KoinConst
import com.wealthvault.data_store.DataStoreBuilder
import com.wealthvault.data_store.TokenStore
import org.koin.core.qualifier.named
import org.koin.dsl.module



object DataStoreModule {
    val allModules = module {
        single<DataStore<Preferences>>(named(KoinConst.DataStore.DEFAULT)) {
            DataStoreBuilder().buildDefaultDataStore()
        }

        single {
            TokenStore(get(named(KoinConst.DataStore.DEFAULT)))
        }
    }
}

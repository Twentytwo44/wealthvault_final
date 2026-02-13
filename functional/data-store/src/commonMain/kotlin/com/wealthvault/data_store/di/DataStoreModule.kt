package com.wealthvault.data_store.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.wealthvault.data_store.DataStoreBuilder
import com.wealthvault.data_store.TokenStore
import org.koin.dsl.module


object DataStoreModule {
    val allModules = module {
        single<DataStore<Preferences>>() {
            DataStoreBuilder().buildDefaultDataStore()
        }

        single {
            TokenStore(get())
        }
    }
}

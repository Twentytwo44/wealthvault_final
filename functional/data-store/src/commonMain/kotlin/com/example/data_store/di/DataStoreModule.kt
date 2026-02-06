package com.example.data_store.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.core.KoinConst
import com.example.data_store.DataStoreBuilder
import com.example.data_store.TokenStore
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
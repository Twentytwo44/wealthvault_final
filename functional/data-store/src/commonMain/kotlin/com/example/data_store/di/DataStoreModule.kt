package com.example.data_store.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.data_store.DataStoreBuilder
import org.koin.core.qualifier.named
import org.koin.dsl.module


object DataStoreModule {
    val allModules = module {
        single<DataStore<Preferences>>(named(KoinConst.DataStore.DEFAULT)) {
            DataStoreBuilder().buildDefaultDataStore()
        }
    }
}
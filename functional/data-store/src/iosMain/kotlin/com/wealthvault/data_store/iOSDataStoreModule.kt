package com.wealthvault.data_store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.dsl.module

object iosDataStoreModule {
    val allModules = module {

        single<DataStore<Preferences>> {
            createDataStore()
        }
    }
}

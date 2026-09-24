package com.wealthvault.security.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.dsl.module

object iosDataStoreModule {
    val allModules = module {
        single<SecureStorage> { IosSecureStorage() }

        single<DataStore<Preferences>> {
            createDataStore()
        }
    }
}

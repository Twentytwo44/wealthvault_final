package com.wealthvault.security.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object androidDataStoreModule  {

    val allModules = module {
        single<SecureStorage> { AndroidSecureStorage(androidContext()) }
        single<DataStore<Preferences>> {
    createAndroidDataStore(androidContext())
}}
}

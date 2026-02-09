package com.wealthvault.data_store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

internal class DataStoreBuilder {

    companion object {
        private const val DEFAULT_DATA_STORE_PATH = "default.preferences_pb"
    }

    fun buildDefaultDataStore(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath {
            DEFAULT_DATA_STORE_PATH.toPath()
        }
    }
}

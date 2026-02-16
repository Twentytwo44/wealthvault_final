package com.wealthvault.data_store
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import java.io.File

fun createAndroidDataStore(context: Context): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create(
        produceFile = {
            File(context.filesDir, "default.preferences_pb")
        }
    )
}

actual fun createDataStore(): DataStore<Preferences> {
    error("Use android module injection")
}

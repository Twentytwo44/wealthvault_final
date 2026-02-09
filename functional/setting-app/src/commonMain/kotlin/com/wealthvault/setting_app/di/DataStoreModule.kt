package com.wealthvault.setting_app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.example.core.KoinConst
import com.example.setting_app.SettingsManager
import okio.Path.Companion.toPath
import org.koin.core.qualifier.named
import org.koin.dsl.module




object AppSettingModule {
    val allModules = module {
        single<DataStore<Preferences>>(named(KoinConst.DataStore.APP_SETTING)) {
            // แนะนำให้สร้างไฟล์ชื่อ "settings.preferences_pb" แยกจากไฟล์ token
            PreferenceDataStoreFactory.createWithPath {
                "settings.preferences_pb".toPath()
            }
        }

        single { SettingsManager(get(named(KoinConst.DataStore.APP_SETTING))) }
    }
}

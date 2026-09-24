package com.wealthvault.app.di

import android.content.Context
import com.wealthvault.data.auth.AuthAndroidDataModule
import com.wealthvault.database.androidDatabaseModule
import com.wealthvault.security.androidSecurityStorageModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Android's platform graph is assembled here so the launcher only starts the
 * application and does not know about data or storage implementations.
 */
fun initAndroidKoin(context: Context) {
    startKoin {
        androidContext(context)
        androidLogger()
        modules(
            AllModules.modules +
                androidSecurityStorageModule +
                androidDatabaseModule +
                AuthAndroidDataModule.allModules,
        )
    }
}

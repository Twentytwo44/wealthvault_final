package com.wealthvault.app

import ForgetModule
import android.app.Application
import com.google.firebase.FirebaseApp
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.security.androidSecurityStorageModule
import com.wealthvault.database.androidDatabaseModule
import com.wealthvault.app.di.AllModules
import com.wealthvault.data.auth.AuthAndroidDataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        try {
            FirebaseApp.initializeApp(this)
        } catch (error: Throwable) {
            platformLogger().error("Firebase initialization failed", error)
        }
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            androidLogger()
            modules(
                AllModules.modules +
                    androidSecurityStorageModule +
                    androidDatabaseModule +
                    AuthAndroidDataModule.allModules +
                    ForgetModule.allModules,
            )
        }
    }
}

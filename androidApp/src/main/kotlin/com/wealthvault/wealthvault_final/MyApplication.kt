package com.wealthvault.wealthvault_final

import ForgetModule
import android.app.Application
import com.google.firebase.FirebaseApp
import com.wealthvault.data_store.androidDataStoreModule
import com.wealthvault.financiallist.di.financiallistModule
import com.wealthvault.google_auth.di.GoogleAuthAndroidModule
import com.wealthvault.wealthvault_final.di.AllModules
import com.wealthvault_final.notification.di.NotificationModule
import com.wealthvault.di.dashboardModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        try {
            FirebaseApp.initializeApp(this)
        } catch (error: Throwable) {
            error.printStackTrace()
        }
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            androidLogger()
            modules(
                AllModules.modules +
                    androidDataStoreModule.allModules +
                    GoogleAuthAndroidModule.allModules +
                    NotificationModule.allModules +
                    financiallistModule +
                    dashboardModule +
                    ForgetModule.allModules,
            )
        }
    }
}

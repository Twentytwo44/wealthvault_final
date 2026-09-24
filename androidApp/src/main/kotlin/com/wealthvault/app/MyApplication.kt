package com.wealthvault.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.app.di.initAndroidKoin

class MyApplication : Application() {
    override fun onCreate() {
        try {
            FirebaseApp.initializeApp(this)
        } catch (error: Throwable) {
            platformLogger().error("Firebase initialization failed", error)
        }
        super.onCreate()

        initAndroidKoin(this)
    }
}

package com.example.wealthvault_final

import android.app.Application
import com.example.wealthvault_final.di.AllModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            androidLogger()
            modules(AllModules.modules)
        }
}
}
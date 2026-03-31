package com.wealthvault.wealthvault_final

import android.app.Application
import com.google.firebase.FirebaseApp
import com.wealthvault.data_store.androidDataStoreModule
import com.wealthvault.financiallist.di.financiallistModule
import com.wealthvault.google_auth.di.GoogleAuthAndroidModule
import com.wealthvault.wealthvault_final.di.AllModules
import com.wealthvault_final.notification.di.NotificationModule
// 🌟 1. อย่าลืม Import dashboardModule เข้ามานะครับ (แก้ package ให้ตรงกับที่คุณ Champ สร้างไว้)
import com.wealthvault.di.dashboardModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        try {
            FirebaseApp.initializeApp(this)
            println("✅ Firebase Initialized from MainActivity")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            androidLogger()

            // 🌟 2. เพิ่ม dashboardModule ต่อท้าย และจัดบรรทัดให้อ่านง่ายขึ้นครับ
            modules(
                AllModules.modules +
                        androidDataStoreModule.allModules +
                        GoogleAuthAndroidModule.allModules +
                        NotificationModule.allModules +
                        financiallistModule +
                        dashboardModule // 👈 เสียบสายไฟตรงนี้!
            )
        }
    }
}
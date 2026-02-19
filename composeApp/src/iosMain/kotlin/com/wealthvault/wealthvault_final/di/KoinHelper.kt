package com.example.wealthvault_final.di


import com.example.google_auth.di.GoogleAuthIOSModule
import com.wealthvault.data_store.iosDataStoreModule
import com.wealthvault.wealthvault_final.di.AllModules
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.UIKit.UIViewController

fun initKoin(controller: UIViewController) {
    startKoin {
        modules(
            AllModules.modules +
                    iosDataStoreModule.allModules +
                    GoogleAuthIOSModule.allModules +
                    module {
                        single { controller }   // 👈 ตรงนี้สำคัญมาก
                    }
        )
    }
}

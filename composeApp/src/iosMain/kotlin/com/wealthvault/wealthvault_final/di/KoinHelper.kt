package com.wealthvault.wealthvault_final.di


import com.wealthvault.data_store.iosDataStoreModule
import com.wealthvault.google_auth.di.GoogleAuthIOSModule
import com.wealthvault_final.line_auth.SwiftLineAuth
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.UIKit.UIViewController

fun initKoin(controller: UIViewController, swiftLineAuth: SwiftLineAuth) {    startKoin {
        modules(
            AllModules.modules +
                    iosDataStoreModule.allModules +
                    GoogleAuthIOSModule.allModules +
                    module {
                        single { controller }
                        single<SwiftLineAuth> { swiftLineAuth }
                    }

        )
    }
}

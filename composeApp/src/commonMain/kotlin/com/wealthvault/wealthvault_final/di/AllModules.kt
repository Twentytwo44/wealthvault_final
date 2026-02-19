package com.wealthvault.wealthvault_final.di

import LoginModule
import com.example.google_auth.di.GoogleAuthMainModule
import com.wealthvault.`auth-api`.di.ApiModule
import com.wealthvault.data_store.di.DataStoreModule
import org.koin.core.module.Module


object AllModules {
    val modules = arrayListOf<Module>().apply {
        add(ApiModule.allModules)
        add(DataStoreModule.allModules)
        add(LoginModule.allModules)
        add(GoogleAuthMainModule.allModules)


    }
}
//
//object AllModules {
//    val modules = arrayListOf<Module>().apply {
////        add(NavigationModule.allModules)
////        add(ApiModule.allModules)
////        add(DataStoreModule.allModules)
////        add(HomeModule.allModules)
//    }
//}

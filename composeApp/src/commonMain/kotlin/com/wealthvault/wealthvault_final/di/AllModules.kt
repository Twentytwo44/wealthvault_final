package com.wealthvault.wealthvault_final.di

import IntroModule
import com.wealthvault.`auth-api`.di.ApiModule
import com.wealthvault.data_store.di.DataStoreModule
import org.koin.core.module.Module


object AllModules {
    val modules = arrayListOf<Module>().apply {
        add(ApiModule.allModules)
        add(DataStoreModule.allModules)
        add(IntroModule.allModules)

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

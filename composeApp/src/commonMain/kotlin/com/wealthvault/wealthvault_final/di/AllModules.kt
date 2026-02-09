package com.wealthvault.wealthvault_final.di

import com.wealthvault.data_store.di.DataStoreModule
import com.wealthvault.functional.api.di.ApiModule
import org.koin.core.module.Module


object AllModules {
    val modules = arrayListOf<Module>().apply {
        add(ApiModule.allModules)
        add(DataStoreModule.allModules)

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

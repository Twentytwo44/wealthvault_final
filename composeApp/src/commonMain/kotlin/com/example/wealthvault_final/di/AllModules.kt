package com.example.wealthvault_final.di

import com.example.data_store.di.DataStoreModule
import org.koin.core.module.Module
import com.example.functional.api.di.ApiModule



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
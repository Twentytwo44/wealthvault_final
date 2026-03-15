package com.wealthvault.wealthvault_final.di

import LoginModule
import com.example.account_api.di.AccountApiModule
import com.example.building_api.di.BuildingApiModule
import com.example.cash_api.di.CashApiModule
import com.example.google_auth.di.GoogleAuthMainModule
import com.example.insurance_api.di.InsuranceApiModule
import com.example.investment_api.di.InvestmentApiModule
import com.example.land_api.di.LandApiModule
import com.example.liability_api.di.LiabilityApiModule
import com.example.`user-api`.di.UserApiModule
import com.wealthvault.`auth-api`.di.ApiModule
import com.wealthvault.data_store.di.DataStoreModule
import org.koin.core.module.Module


object AllModules {
    val modules = arrayListOf<Module>().apply {

        add(DataStoreModule.allModules)
        add(LoginModule.allModules)

        // function:api
        add(GoogleAuthMainModule.allModules)
        add(AccountApiModule.allModules)
        add(ApiModule.allModules)
        add(BuildingApiModule.allModules)
        add(CashApiModule.allModules)
        add(InsuranceApiModule.allModules)
        add(InvestmentApiModule.allModules)
        add(LandApiModule.allModules)
        add(LiabilityApiModule.allModules)
        add(UserApiModule.allModules)



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

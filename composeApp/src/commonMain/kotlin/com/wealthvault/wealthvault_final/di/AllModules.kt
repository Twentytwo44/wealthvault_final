package com.wealthvault.wealthvault_final.di

import LoginModule
import RegisterModule
import com.wealthvault.account_api.di.AccountApiModule
import com.wealthvault.`auth-api`.di.ApiModule
import com.wealthvault.building_api.di.BuildingApiModule
import com.wealthvault.cash_api.di.CashApiModule
import com.wealthvault.data_store.di.DataStoreModule
import com.wealthvault.google_auth.di.GoogleAuthMainModule
import com.wealthvault.insurance_api.di.InsuranceApiModule
import com.wealthvault.investment_api.di.InvestmentApiModule
import com.wealthvault.land_api.di.LandApiModule
import com.wealthvault.liability_api.di.LiabilityApiModule
import com.wealthvault.profile.di.ProfileModule
import com.wealthvault.`user-api`.di.UserApiModule
import com.wealthvault_final.setup_api.di.GlobalApiModule
import org.koin.core.module.Module


object AllModules {
    val modules = arrayListOf<Module>().apply {

        add(DataStoreModule.allModules)
        add(LoginModule.allModules)

        // setup httpclientbuilder
        add(GlobalApiModule.allModules)
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
        add(GlobalApiModule.allModules)

        add(RegisterModule.allModules)
        add(ProfileModule.allModules)


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

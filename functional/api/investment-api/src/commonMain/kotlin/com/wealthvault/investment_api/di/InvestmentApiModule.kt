package com.wealthvault.investment_api.di

import com.wealthvault.core.KoinConst
import com.wealthvault.investment_api.createinvestment.CreateInvestmentApi
import com.wealthvault.investment_api.createinvestment.CreateInvestmentApiImpl

import com.wealthvault.investment_api.deleteinvestment.DeleteInvestmentApi
import com.wealthvault.investment_api.deleteinvestment.DeleteInvestmentApiImpl
import com.wealthvault.investment_api.getinvestment.GetInvestmentApi
import com.wealthvault.investment_api.getinvestment.GetInvestmentApiImpl
import com.wealthvault.investment_api.getinvestmentbyid.GetInvestmentByIdApi
import com.wealthvault.investment_api.getinvestmentbyid.GetInvestmentByIdApiImpl
import com.wealthvault.investment_api.updateinvestment.UpdateInvestmentApi
import com.wealthvault.investment_api.updateinvestment.UpdateInvestmentApiImpl
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

object InvestmentApiModule {
    val allModules = module {
        single<Json>(named(KoinConst.KotlinSerialization.GLOBAL)) {
            Json {
                ignoreUnknownKeys = true
            }
        }

        single<CreateInvestmentApi> { CreateInvestmentApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<GetInvestmentApi> { GetInvestmentApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<GetInvestmentByIdApi> { GetInvestmentByIdApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<UpdateInvestmentApi> { UpdateInvestmentApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<DeleteInvestmentApi> { DeleteInvestmentApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }



    }


}

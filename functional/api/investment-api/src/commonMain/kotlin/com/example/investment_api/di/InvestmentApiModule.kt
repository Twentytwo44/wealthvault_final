package com.example.investment_api.di

import com.example.investment_api.createcash.CreateInvestmentApi
import com.example.investment_api.createcash.CreateInvestmentApiImpl
import com.example.investment_api.deleteinvestment.DeleteInvestmentApi
import com.example.investment_api.deleteinvestment.DeleteInvestmentApiImpl
import com.example.investment_api.getinvestment.GetInvestmentApi
import com.example.investment_api.getinvestment.GetInvestmentApiImpl
import com.example.investment_api.getinvestmentbyid.GetInvestmentByIdApi
import com.example.investment_api.getinvestmentbyid.GetInvestmentByIdApiImpl
import com.example.investment_api.updateinvestment.UpdateInvestmentApi
import com.example.investment_api.updateinvestment.UpdateInvestmentApiImpl
import com.wealthvault.core.KoinConst
import org.koin.core.qualifier.named
import org.koin.dsl.module


object InvestmentApiModule {
    val allModules = module {

        single<CreateInvestmentApi> { CreateInvestmentApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<GetInvestmentApi> { GetInvestmentApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<GetInvestmentByIdApi> { GetInvestmentByIdApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<UpdateInvestmentApi> { UpdateInvestmentApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<DeleteInvestmentApi> { DeleteInvestmentApiImpl(get(named(KoinConst.Ktor.USER))) }



    }


}

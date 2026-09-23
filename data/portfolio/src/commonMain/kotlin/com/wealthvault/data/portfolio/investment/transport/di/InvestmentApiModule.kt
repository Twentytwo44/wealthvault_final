package com.wealthvault.data.portfolio.investment.transport.di

import com.wealthvault.core.KoinConst
import io.ktor.client.HttpClient
import com.wealthvault.data.portfolio.investment.transport.createinvestment.CreateInvestmentApi
import com.wealthvault.data.portfolio.investment.transport.createinvestment.CreateInvestmentApiImpl

import com.wealthvault.data.portfolio.investment.transport.deleteinvestment.DeleteInvestmentApi
import com.wealthvault.data.portfolio.investment.transport.deleteinvestment.DeleteInvestmentApiImpl
import com.wealthvault.data.portfolio.investment.transport.getinvestment.GetInvestmentApi
import com.wealthvault.data.portfolio.investment.transport.getinvestment.GetInvestmentApiImpl
import com.wealthvault.data.portfolio.investment.transport.getinvestmentbyid.GetInvestmentByIdApi
import com.wealthvault.data.portfolio.investment.transport.getinvestmentbyid.GetInvestmentByIdApiImpl
import com.wealthvault.data.portfolio.investment.transport.updateinvestment.UpdateInvestmentApi
import com.wealthvault.data.portfolio.investment.transport.updateinvestment.UpdateInvestmentApiImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

object InvestmentApiModule {
    val allModules = module {
        single<CreateInvestmentApi> { CreateInvestmentApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetInvestmentApi> { GetInvestmentApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetInvestmentByIdApi> { GetInvestmentByIdApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UpdateInvestmentApi> { UpdateInvestmentApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<DeleteInvestmentApi> { DeleteInvestmentApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }



    }


}

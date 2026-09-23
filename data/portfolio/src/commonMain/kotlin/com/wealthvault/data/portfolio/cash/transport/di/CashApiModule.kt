package com.wealthvault.data.portfolio.cash.transport.di



import com.wealthvault.data.portfolio.cash.transport.createcash.CreateCashApi
import com.wealthvault.data.portfolio.cash.transport.createcash.CreateCashApiImpl
import com.wealthvault.data.portfolio.cash.transport.deletecash.DeleteCashApi
import com.wealthvault.data.portfolio.cash.transport.deletecash.DeleteCashApiImpl
import com.wealthvault.data.portfolio.cash.transport.getcash.GetCashApi
import com.wealthvault.data.portfolio.cash.transport.getcash.GetCashApiImpl
import com.wealthvault.data.portfolio.cash.transport.getcashtbyid.GetCashByIdApi
import com.wealthvault.data.portfolio.cash.transport.getcashtbyid.GetCashByIdApiImpl
import com.wealthvault.data.portfolio.cash.transport.updatecash.UpdateCashApi
import com.wealthvault.data.portfolio.cash.transport.updatecash.UpdateCashApiImpl
import com.wealthvault.core.KoinConst
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module


object CashApiModule {
    val allModules = module {
        single<CreateCashApi> { CreateCashApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetCashApi> { GetCashApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetCashByIdApi> { GetCashByIdApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UpdateCashApi> { UpdateCashApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<DeleteCashApi> { DeleteCashApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }



    }


}

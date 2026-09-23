package com.wealthvault.data.portfolio.land.transport.di

import com.wealthvault.core.KoinConst
import io.ktor.client.HttpClient
import com.wealthvault.data.portfolio.investment.transport.createcash.CreateLandApi
import com.wealthvault.data.portfolio.investment.transport.createcash.CreateLandApiImpl
import com.wealthvault.data.portfolio.land.transport.deleteland.DeleteLandApi
import com.wealthvault.data.portfolio.land.transport.deleteland.DeleteLandApiImpl
import com.wealthvault.data.portfolio.land.transport.getland.GetLandApi
import com.wealthvault.data.portfolio.land.transport.getland.GetLandApiImpl
import com.wealthvault.data.portfolio.land.transport.getlandbyid.GetLandByIdApi
import com.wealthvault.data.portfolio.land.transport.getlandbyid.GetLandByIdApiImpl
import com.wealthvault.data.portfolio.land.transport.updateland.UpdateLandApi
import com.wealthvault.data.portfolio.land.transport.updateland.UpdateLandApiImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module


object LandApiModule {
    val allModules = module {
        single<CreateLandApi> { CreateLandApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetLandApi> { GetLandApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetLandByIdApi> { GetLandByIdApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UpdateLandApi> { UpdateLandApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<DeleteLandApi> { DeleteLandApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }



    }


}

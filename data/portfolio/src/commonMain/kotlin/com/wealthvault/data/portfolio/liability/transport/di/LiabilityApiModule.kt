package com.wealthvault.data.portfolio.liability.transport.di


import com.wealthvault.core.KoinConst
import io.ktor.client.HttpClient
import com.wealthvault.data.portfolio.investment.transport.createcash.CreateLiabilityApi
import com.wealthvault.data.portfolio.investment.transport.createcash.CreateLiabilityApiImpl
import com.wealthvault.data.portfolio.liability.transport.deleteliability.DeleteLiabilityApi
import com.wealthvault.data.portfolio.liability.transport.deleteliability.DeleteLiabilityApiImpl
import com.wealthvault.data.portfolio.liability.transport.getliability.GetLiabilityApi
import com.wealthvault.data.portfolio.liability.transport.getliability.GetLiabilityApiImpl
import com.wealthvault.data.portfolio.liability.transport.getliabilitybyid.GetLiabilityByIdApi
import com.wealthvault.data.portfolio.liability.transport.getliabilitybyid.GetLiabilityByIdApiImpl
import com.wealthvault.data.portfolio.liability.transport.updateliability.UpdateLiabilityApi
import com.wealthvault.data.portfolio.liability.transport.updateliability.UpdateLiabilityApiImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module


object LiabilityApiModule {
    val allModules = module {
        single<CreateLiabilityApi> { CreateLiabilityApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetLiabilityApi> { GetLiabilityApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetLiabilityByIdApi> { GetLiabilityByIdApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UpdateLiabilityApi> { UpdateLiabilityApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<DeleteLiabilityApi> { DeleteLiabilityApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }



    }


}

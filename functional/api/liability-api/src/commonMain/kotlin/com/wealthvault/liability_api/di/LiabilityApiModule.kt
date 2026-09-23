package com.wealthvault.liability_api.di


import com.wealthvault.core.KoinConst
import io.ktor.client.HttpClient
import com.wealthvault.investment_api.createcash.CreateLiabilityApi
import com.wealthvault.investment_api.createcash.CreateLiabilityApiImpl
import com.wealthvault.liability_api.deleteliability.DeleteLiabilityApi
import com.wealthvault.liability_api.deleteliability.DeleteLiabilityApiImpl
import com.wealthvault.liability_api.getliability.GetLiabilityApi
import com.wealthvault.liability_api.getliability.GetLiabilityApiImpl
import com.wealthvault.liability_api.getliabilitybyid.GetLiabilityByIdApi
import com.wealthvault.liability_api.getliabilitybyid.GetLiabilityByIdApiImpl
import com.wealthvault.liability_api.updateliability.UpdateLiabilityApi
import com.wealthvault.liability_api.updateliability.UpdateLiabilityApiImpl
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module


object LiabilityApiModule {
    val allModules = module {
        single<Json>(named(KoinConst.KotlinSerialization.GLOBAL)) {
            Json {
                ignoreUnknownKeys = true
            }
        }

        single<CreateLiabilityApi> { CreateLiabilityApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetLiabilityApi> { GetLiabilityApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetLiabilityByIdApi> { GetLiabilityByIdApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UpdateLiabilityApi> { UpdateLiabilityApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<DeleteLiabilityApi> { DeleteLiabilityApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }



    }


}

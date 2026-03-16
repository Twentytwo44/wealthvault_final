package com.example.liability_api.di


import com.example.investment_api.createcash.CreateLiabilityApi
import com.example.investment_api.createcash.CreateLiabilityApiImpl
import com.example.liability_api.deleteliability.DeleteLiabilityApi
import com.example.liability_api.deleteliability.DeleteLiabilityApiImpl
import com.example.liability_api.getliability.GetLiabilityApi
import com.example.liability_api.getliability.GetLiabilityApiImpl
import com.example.liability_api.getliabilitybyid.GetLiabilityByIdApi
import com.example.liability_api.getliabilitybyid.GetLiabilityByIdApiImpl
import com.example.liability_api.updateliability.UpdateLiabilityApi
import com.example.liability_api.updateliability.UpdateLiabilityApiImpl
import com.wealthvault.core.KoinConst
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

        single<CreateLiabilityApi> { CreateLiabilityApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<GetLiabilityApi> { GetLiabilityApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<GetLiabilityByIdApi> { GetLiabilityByIdApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<UpdateLiabilityApi> { UpdateLiabilityApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<DeleteLiabilityApi> { DeleteLiabilityApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }



    }


}

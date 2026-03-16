package com.example.land_api.di

import com.example.investment_api.createcash.CreateLandApi
import com.example.investment_api.createcash.CreateLandApiImpl
import com.example.land_api.deleteland.DeleteLandApi
import com.example.land_api.deleteland.DeleteLandApiImpl
import com.example.land_api.getland.GetLandApi
import com.example.land_api.getland.GetLandApiImpl
import com.example.land_api.getlandbyid.GetLandByIdApi
import com.example.land_api.getlandbyid.GetLandByIdApiImpl
import com.example.land_api.updateland.UpdateLandApi
import com.example.land_api.updateland.UpdateLandApiImpl
import com.wealthvault.core.KoinConst
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module


object LandApiModule {
    val allModules = module {
        single<Json>(named(KoinConst.KotlinSerialization.GLOBAL)) {
            Json {
                ignoreUnknownKeys = true
            }
        }

        single<CreateLandApi> { CreateLandApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<GetLandApi> { GetLandApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<GetLandByIdApi> { GetLandByIdApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<UpdateLandApi> { UpdateLandApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<DeleteLandApi> { DeleteLandApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }



    }


}

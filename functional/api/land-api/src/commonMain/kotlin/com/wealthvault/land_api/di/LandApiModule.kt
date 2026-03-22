package com.wealthvault.land_api.di

import com.wealthvault.core.KoinConst
import com.wealthvault.investment_api.createcash.CreateLandApi
import com.wealthvault.investment_api.createcash.CreateLandApiImpl
import com.wealthvault.land_api.deleteland.DeleteLandApi
import com.wealthvault.land_api.deleteland.DeleteLandApiImpl
import com.wealthvault.land_api.getland.GetLandApi
import com.wealthvault.land_api.getland.GetLandApiImpl
import com.wealthvault.land_api.getlandbyid.GetLandByIdApi
import com.wealthvault.land_api.getlandbyid.GetLandByIdApiImpl
import com.wealthvault.land_api.updateland.UpdateLandApi
import com.wealthvault.land_api.updateland.UpdateLandApiImpl
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

package com.wealthvault.building_api.di

import com.wealthvault.building_api.deletebuilding.DeleteBuildingApi
import com.wealthvault.building_api.deletebuilding.DeleteBuildingApiImpl
import com.wealthvault.building_api.getbuilding.GetBuildingApi
import com.wealthvault.building_api.getbuilding.GetBuildingApiImpl
import com.wealthvault.building_api.getbuildingbyid.GetBuildingByIdApi
import com.wealthvault.building_api.getbuildingbyid.GetBuildingByIdApiImpl
import com.wealthvault.building_api.updatebuilding.UpdateBuildingApi
import com.wealthvault.building_api.updatebuilding.UpdateBuildingApiImpl
import com.wealthvault.core.KoinConst
import com.wealthvault.investment_api.createcash.CreateBuildingApi
import com.wealthvault.investment_api.createcash.CreateBuildingApiImpl
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module



object BuildingApiModule {
    val allModules = module {
        single<Json>(named(KoinConst.KotlinSerialization.GLOBAL)) {
            Json {
                ignoreUnknownKeys = true
            }
        }


        single<CreateBuildingApi> { CreateBuildingApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<GetBuildingApi> { GetBuildingApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<GetBuildingByIdApi> { GetBuildingByIdApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<UpdateBuildingApi> { UpdateBuildingApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }
        single<DeleteBuildingApi> { DeleteBuildingApiImpl(get(named(KoinConst.Ktor.GLOBAL))) }

    }


}

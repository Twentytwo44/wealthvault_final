package com.example.building_api.di

import com.example.building_api.deletebuilding.DeleteBuildingApi
import com.example.building_api.deletebuilding.DeleteBuildingApiImpl
import com.example.building_api.getbuilding.GetBuildingApi
import com.example.building_api.getbuilding.GetBuildingApiImpl
import com.example.building_api.getbuildingbyid.GetBuildingByIdApi
import com.example.building_api.getbuildingbyid.GetBuildingByIdApiImpl
import com.example.building_api.updatebuilding.UpdateBuildingApi
import com.example.building_api.updatebuilding.UpdateBuildingApiImpl
import com.example.investment_api.createcash.CreateBuildingApi
import com.example.investment_api.createcash.CreateBuildingApiImpl
import com.wealthvault.core.KoinConst
import org.koin.core.qualifier.named
import org.koin.dsl.module


object BuildingApiModule {
    val allModules = module {

        single<CreateBuildingApi> { CreateBuildingApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<GetBuildingApi> { GetBuildingApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<GetBuildingByIdApi> { GetBuildingByIdApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<UpdateBuildingApi> { UpdateBuildingApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<DeleteBuildingApi> { DeleteBuildingApiImpl(get(named(KoinConst.Ktor.USER))) }



    }


}

package com.wealthvault.data.portfolio.building.transport.di

import com.wealthvault.data.portfolio.building.transport.deletebuilding.DeleteBuildingApi
import com.wealthvault.data.portfolio.building.transport.deletebuilding.DeleteBuildingApiImpl
import com.wealthvault.data.portfolio.building.transport.getbuilding.GetBuildingApi
import com.wealthvault.data.portfolio.building.transport.getbuilding.GetBuildingApiImpl
import com.wealthvault.data.portfolio.building.transport.getbuildingbyid.GetBuildingByIdApi
import com.wealthvault.data.portfolio.building.transport.getbuildingbyid.GetBuildingByIdApiImpl
import com.wealthvault.data.portfolio.building.transport.updatebuilding.UpdateBuildingApi
import com.wealthvault.data.portfolio.building.transport.updatebuilding.UpdateBuildingApiImpl
import com.wealthvault.core.KoinConst
import io.ktor.client.HttpClient
import com.wealthvault.data.portfolio.investment.transport.createcash.CreateBuildingApi
import com.wealthvault.data.portfolio.investment.transport.createcash.CreateBuildingApiImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module



object BuildingApiModule {
    val allModules = module {
        single<CreateBuildingApi> { CreateBuildingApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetBuildingApi> { GetBuildingApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetBuildingByIdApi> { GetBuildingByIdApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UpdateBuildingApi> { UpdateBuildingApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<DeleteBuildingApi> { DeleteBuildingApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }

    }


}

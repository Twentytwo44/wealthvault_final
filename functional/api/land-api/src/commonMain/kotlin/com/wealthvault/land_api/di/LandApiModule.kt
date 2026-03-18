package com.wealthvault.land_api.di

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
import com.wealthvault.core.KoinConst
import org.koin.core.qualifier.named
import org.koin.dsl.module


object LandApiModule {
    val allModules = module {

        single<CreateLandApi> { CreateLandApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<GetLandApi> { GetLandApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<GetLandByIdApi> { GetLandByIdApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<UpdateLandApi> { UpdateLandApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<DeleteLandApi> { DeleteLandApiImpl(get(named(KoinConst.Ktor.USER))) }



    }


}

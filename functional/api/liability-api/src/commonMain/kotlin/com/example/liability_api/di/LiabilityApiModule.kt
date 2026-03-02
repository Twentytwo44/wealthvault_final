package com.example.liability_api.di


import com.example.investment_api.createcash.CreateLiabilityApiImpl
import com.wealthvault.core.KoinConst
import org.koin.core.qualifier.named
import org.koin.dsl.module


object LiabilityApiModule {
    val allModules = module {

        single<CreateLiabilityApi> { CreateLiabilityApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<GetLiabilityApi> { GetLiabilityApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<GetLiabilityByIdApi> { GetLiabilityByIdApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<UpdateLiabilityApi> { UpdateLiabilityApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<DeleteLiabilityApi> { DeleteLiabilityApiImpl(get(named(KoinConst.Ktor.USER))) }



    }


}

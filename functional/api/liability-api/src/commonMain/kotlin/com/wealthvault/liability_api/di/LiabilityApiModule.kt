package com.wealthvault.liability_api.di


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

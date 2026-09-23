package com.wealthvault.data.portfolio.insurance.transport.di




import com.wealthvault.core.KoinConst
import io.ktor.client.HttpClient
import com.wealthvault.data.portfolio.insurance.transport.createcash.CreateInsuranceApi
import com.wealthvault.data.portfolio.insurance.transport.createcash.CreateInsuranceApiImpl
import com.wealthvault.data.portfolio.insurance.transport.deleteinsurance.DeleteInsuranceApi
import com.wealthvault.data.portfolio.insurance.transport.deleteinsurance.DeleteInsuranceApiImpl
import com.wealthvault.data.portfolio.insurance.transport.getinsurance.GetInsuranceApi
import com.wealthvault.data.portfolio.insurance.transport.getinsurance.GetInsuranceApiImpl
import com.wealthvault.data.portfolio.insurance.transport.getinsurancetbyid.GetInsuranceByIdApi
import com.wealthvault.data.portfolio.insurance.transport.getinsurancetbyid.GetInsuranceByIdApiImpl
import com.wealthvault.data.portfolio.insurance.transport.updateinsurance.UpdateInsuranceApi
import com.wealthvault.data.portfolio.insurance.transport.updateinsurance.UpdateInsuranceApiImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module


object InsuranceApiModule {
    val allModules = module {
        single<CreateInsuranceApi> { CreateInsuranceApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetInsuranceApi> { GetInsuranceApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetInsuranceByIdApi> { GetInsuranceByIdApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UpdateInsuranceApi> { UpdateInsuranceApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<DeleteInsuranceApi> { DeleteInsuranceApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }



    }


}

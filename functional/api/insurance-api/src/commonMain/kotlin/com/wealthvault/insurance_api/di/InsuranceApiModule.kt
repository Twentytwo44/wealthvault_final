package com.wealthvault.insurance_api.di




import com.wealthvault.core.KoinConst
import io.ktor.client.HttpClient
import com.wealthvault.insurance_api.createcash.CreateInsuranceApi
import com.wealthvault.insurance_api.createcash.CreateInsuranceApiImpl
import com.wealthvault.insurance_api.deleteinsurance.DeleteInsuranceApi
import com.wealthvault.insurance_api.deleteinsurance.DeleteInsuranceApiImpl
import com.wealthvault.insurance_api.getinsurance.GetInsuranceApi
import com.wealthvault.insurance_api.getinsurance.GetInsuranceApiImpl
import com.wealthvault.insurance_api.getinsurancetbyid.GetInsuranceByIdApi
import com.wealthvault.insurance_api.getinsurancetbyid.GetInsuranceByIdApiImpl
import com.wealthvault.insurance_api.updateinsurance.UpdateInsuranceApi
import com.wealthvault.insurance_api.updateinsurance.UpdateInsuranceApiImpl
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module


object InsuranceApiModule {
    val allModules = module {
        single<Json>(named(KoinConst.KotlinSerialization.GLOBAL)) {
            Json {
                ignoreUnknownKeys = true
            }
        }

        single<CreateInsuranceApi> { CreateInsuranceApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetInsuranceApi> { GetInsuranceApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<GetInsuranceByIdApi> { GetInsuranceByIdApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<UpdateInsuranceApi> { UpdateInsuranceApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }
        single<DeleteInsuranceApi> { DeleteInsuranceApiImpl(get<HttpClient>(named(KoinConst.HttpClient.GLOBAL))) }



    }


}

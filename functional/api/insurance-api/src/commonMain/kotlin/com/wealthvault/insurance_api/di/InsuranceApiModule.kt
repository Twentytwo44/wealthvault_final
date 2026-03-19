package com.wealthvault.insurance_api.di




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
import com.wealthvault.core.KoinConst
import org.koin.core.qualifier.named
import org.koin.dsl.module


object InsuranceApiModule {
    val allModules = module {

        single<CreateInsuranceApi> { CreateInsuranceApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<GetInsuranceApi> { GetInsuranceApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<GetInsuranceByIdApi> { GetInsuranceByIdApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<UpdateInsuranceApi> { UpdateInsuranceApiImpl(get(named(KoinConst.Ktor.USER))) }
        single<DeleteInsuranceApi> { DeleteInsuranceApiImpl(get(named(KoinConst.Ktor.USER))) }



    }


}

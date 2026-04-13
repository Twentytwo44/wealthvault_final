package com.wealthvault.insurance_api.getinsurance

import com.wealthvault.insurance_api.model.GetInsuranceResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetInsuranceApi {
    @GET("asset/insurance/")
    suspend fun getInsurance(): GetInsuranceResponse
}

package com.wealthvault.insurance_api.getinsurance

import com.wealthvault.insurance_api.model.GetInsuranceResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetInsuranceApi {
    @GET("ic_nav_asset/insurance")
    suspend fun getInsurance(): GetInsuranceResponse
}

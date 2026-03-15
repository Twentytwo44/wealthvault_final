package com.example.insurance_api.getinsurance

import com.example.insurance_api.model.GetInsuranceResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetInsuranceApi {
    @GET("ic_nav_asset/insurance")
    suspend fun getInsurance(): GetInsuranceResponse
}

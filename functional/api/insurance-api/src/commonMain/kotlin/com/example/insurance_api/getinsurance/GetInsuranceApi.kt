package com.example.insurance_api.getinsurance

import com.example.insurance_api.model.GetInsuranceResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetInsuranceApi {
    @GET("asset/insurance")
    suspend fun getInsurance(): GetInsuranceResponse
}

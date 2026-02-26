package com.example.insurance_api.getinsurancetbyid

import com.example.insurance_api.model.InsuranceIdResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetInsuranceByIdApi {
    @GET("asset/insurance/{id}")
    suspend fun getInsuranceById(@Path("id") id: String): InsuranceIdResponse
}

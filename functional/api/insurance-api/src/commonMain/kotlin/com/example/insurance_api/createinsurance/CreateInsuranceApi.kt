package com.example.insurance_api.createcash

import com.example.insurance_api.model.InsuranceRequest
import com.example.insurance_api.model.InsuranceResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface CreateInsuranceApi {
    @POST("asset/insurance")
    suspend fun create(@Body request: InsuranceRequest): InsuranceResponse
}

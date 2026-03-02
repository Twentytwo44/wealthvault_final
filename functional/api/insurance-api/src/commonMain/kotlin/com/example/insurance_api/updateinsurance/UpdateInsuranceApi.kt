package com.example.insurance_api.updateinsurance

import com.example.insurance_api.model.InsuranceRequest
import com.example.insurance_api.model.InsuranceResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.Path

interface UpdateInsuranceApi {
    @PATCH("asset/insurance/{id}")
    suspend fun updateInsurance(@Path("id") id: String, @Body request: InsuranceRequest): InsuranceResponse
}

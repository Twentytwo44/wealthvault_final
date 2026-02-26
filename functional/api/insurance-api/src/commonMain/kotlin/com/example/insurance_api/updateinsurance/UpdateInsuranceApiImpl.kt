package com.example.insurance_api.updateinsurance

import com.example.insurance_api.model.InsuranceRequest
import com.example.insurance_api.model.InsuranceResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.patch

class UpdateInsuranceApiImpl(private val ktorfit: Ktorfit) : UpdateInsuranceApi {
    override suspend fun updateInsurance(id: String, request: InsuranceRequest): InsuranceResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.patch("${Config.localhost_android}/asset/insurance/${id}") {

        }.body()
    }
}

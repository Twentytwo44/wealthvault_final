package com.example.insurance_api.getinsurance

import com.example.insurance_api.model.GetInsuranceResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetInsuranceApiImpl(private val ktorfit: Ktorfit) : GetInsuranceApi {
    override suspend fun getInsurance(): GetInsuranceResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}/asset/insurance") {

        }.body()
    }
}

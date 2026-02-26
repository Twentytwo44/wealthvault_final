package com.example.cash_api.createcash


import com.example.insurance_api.model.InsuranceRequest
import com.example.insurance_api.model.InsuranceResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post

class CreateInsuranceApiImpl(private val ktorfit: Ktorfit) : CreateInsuranceApi {
    override suspend fun create(request: InsuranceRequest): InsuranceResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}/asset/insurance") {

        }.body()
    }
}

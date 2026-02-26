package com.example.insurance_api.getinsurancetbyid

import com.example.insurance_api.model.InsuranceIdResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetInsuranceByIdApiImpl(private val ktorfit: Ktorfit) : GetInsuranceByIdApi {
    override suspend fun getInsuranceById(id: String): InsuranceIdResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}/asset/insurance/${id}") {

        }.body()
    }
}

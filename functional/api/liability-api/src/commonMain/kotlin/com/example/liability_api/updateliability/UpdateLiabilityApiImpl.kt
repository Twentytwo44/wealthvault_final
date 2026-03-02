package com.example.liability_api.updateliability


import com.example.liability_api.model.LiabilityRequest
import com.example.liability_api.model.LiabilityResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.patch

class UpdateLiabilityApiImpl(private val ktorfit: Ktorfit) : UpdateLiabilityApi {
    override suspend fun updateLiability(id: String, request: LiabilityRequest): LiabilityResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.patch("${Config.localhost_android}/asset/lia/${id}") {


        }.body()
    }
}

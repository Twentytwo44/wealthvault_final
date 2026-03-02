package com.example.investment_api.createcash



import com.example.liability_api.model.LiabilityRequest
import com.example.liability_api.model.LiabilityResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post

class CreateLiabilityApiImpl(private val ktorfit: Ktorfit) : CreateLiabilityApi {
    override suspend fun create(request: LiabilityRequest): LiabilityResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}/asset/lia") {

        }.body()
    }
}

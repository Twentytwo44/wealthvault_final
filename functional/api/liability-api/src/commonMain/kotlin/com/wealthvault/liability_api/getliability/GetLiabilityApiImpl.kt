package com.wealthvault.liability_api.getliability

import com.wealthvault.config.Config
import com.wealthvault.liability_api.model.GetLiabilityResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetLiabilityApiImpl(private val ktorfit: Ktorfit) : GetLiabilityApi {
    override suspend fun getLiability(): GetLiabilityResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}asset/lia/") {

        }.body()
    }
}

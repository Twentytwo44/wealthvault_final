package com.example.liability_api.getliabilitybyid

import com.example.liability_api.model.LiabilityIdResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetLiabilityByIdApiImpl(private val ktorfit: Ktorfit) : GetLiabilityByIdApi {
    override suspend fun getLiabilityById(id: String): LiabilityIdResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}/asset/lia/${id}") {

        }.body()
    }
}

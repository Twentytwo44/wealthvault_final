package com.example.land_api.getlandbyid

import com.example.land_api.model.LandIdResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetLandByIdApiImpl(private val ktorfit: Ktorfit) : GetLandByIdApi {
    override suspend fun getLandById(id: String): LandIdResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}/asset/land/${id}") {

        }.body()
    }
}

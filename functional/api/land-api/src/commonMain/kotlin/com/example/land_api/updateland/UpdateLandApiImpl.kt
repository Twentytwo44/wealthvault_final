package com.example.land_api.updateland



import com.example.land_api.model.LandRequest
import com.example.land_api.model.LandResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.patch

class UpdateLandApiImpl(private val ktorfit: Ktorfit) : UpdateLandApi {
    override suspend fun updateLand(id: String, request: LandRequest): LandResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.patch("${Config.localhost_android}/asset/land/${id}") {


        }.body()
    }
}

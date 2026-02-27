package com.example.investment_api.createcash




import com.example.land_api.model.LandRequest
import com.example.land_api.model.LandResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post

class CreateLandApiImpl(private val ktorfit: Ktorfit) : CreateLandApi {
    override suspend fun create(request: LandRequest): LandResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}/asset/land") {

        }.body()
    }
}

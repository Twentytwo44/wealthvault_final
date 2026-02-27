package com.example.investment_api.createcash



import com.example.building_api.model.BuildingRequest
import com.example.building_api.model.BuildingResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post

class CreateBuildingApiImpl(private val ktorfit: Ktorfit) : CreateBuildingApi {
    override suspend fun create(request: BuildingRequest): BuildingResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}/asset/building") {

        }.body()
    }
}

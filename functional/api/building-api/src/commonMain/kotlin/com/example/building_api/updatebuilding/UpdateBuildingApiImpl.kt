package com.example.building_api.updatebuilding



import com.example.building_api.model.BuildingRequest
import com.example.building_api.model.BuildingResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.patch

class UpdateBuildingApiImpl(private val ktorfit: Ktorfit) : UpdateBuildingApi {
    override suspend fun updateBuilding(id: String, request: BuildingRequest): BuildingResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.patch("${Config.localhost_android}/asset/building/${id}") {


        }.body()
    }
}

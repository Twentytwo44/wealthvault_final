package com.example.building_api.getbuildingbyid

import com.example.building_api.model.BuildingIdResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetBuildingByIdApiImpl(private val ktorfit: Ktorfit) : GetBuildingByIdApi {
    override suspend fun getBuildingById(id: String): BuildingIdResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}/asset/building/${id}") {

        }.body()
    }
}

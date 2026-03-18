package com.wealthvault.building_api.getbuilding

import com.wealthvault.building_api.model.GetBuildingResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetBuildingApiImpl(private val ktorfit: Ktorfit) : GetBuildingApi {
    override suspend fun getBuilding(): GetBuildingResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}/ic_nav_asset/building") {

        }.body()
    }
}

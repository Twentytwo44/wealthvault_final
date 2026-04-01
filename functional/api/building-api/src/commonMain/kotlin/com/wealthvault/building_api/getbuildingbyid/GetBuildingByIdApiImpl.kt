package com.wealthvault.building_api.getbuildingbyid

import com.wealthvault.building_api.model.BuildingIdResponse // 🌟 ใช้ Model ตัวเดียวกัน
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetBuildingByIdApiImpl(private val ktorfit: Ktorfit) : GetBuildingByIdApi {
    override suspend fun getBuildingById(id: String): BuildingIdResponse {
        val client = ktorfit.httpClient
        return client.get("${Config.localhost_android}asset/building/${id}").body()
    }
}
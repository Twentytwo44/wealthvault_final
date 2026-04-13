package com.wealthvault.building_api.getbuildingbyid

import com.wealthvault.building_api.model.BuildingIdResponse // 🌟 ใช้ Model ของ GET by ID
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetBuildingByIdApi {
    @GET("asset/building/{id}/")
    suspend fun getBuildingById(@Path("id") id: String): BuildingIdResponse
}
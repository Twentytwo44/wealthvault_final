package com.example.building_api.updatebuilding

import com.example.building_api.model.BuildingRequest
import com.example.building_api.model.BuildingResponse

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.Path

interface UpdateBuildingApi {
    @PATCH("asset/building/{id}")
    suspend fun updateBuilding(@Path("id") id: String, @Body request: BuildingRequest): BuildingResponse
}

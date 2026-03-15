package com.example.investment_api.createcash


import com.example.building_api.model.BuildingRequest
import com.example.building_api.model.BuildingResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface CreateBuildingApi {
    @POST("ic_nav_asset/building")
    suspend fun create(@Body request: BuildingRequest): BuildingResponse
}

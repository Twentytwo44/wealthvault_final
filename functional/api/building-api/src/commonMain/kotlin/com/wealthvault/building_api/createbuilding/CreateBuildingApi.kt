package com.wealthvault.investment_api.createcash


import com.wealthvault.building_api.model.BuildingRequest
import com.wealthvault.building_api.model.BuildingResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface CreateBuildingApi {
    @POST("asset/building/")
    suspend fun create(@Body request: BuildingRequest): BuildingResponse
}

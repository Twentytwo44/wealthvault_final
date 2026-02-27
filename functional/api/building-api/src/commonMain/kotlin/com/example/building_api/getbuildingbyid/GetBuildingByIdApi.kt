package com.example.building_api.getbuildingbyid

import com.example.building_api.model.BuildingIdResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetBuildingByIdApi {
    @GET("asset/building/{id}")
    suspend fun getBuildingById(@Path("id") id: String): BuildingIdResponse
}

package com.wealthvault.building_api.getbuilding

import com.wealthvault.building_api.model.GetBuildingResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetBuildingApi {
    @GET("asset/building/")
    suspend fun getBuilding(): GetBuildingResponse
}

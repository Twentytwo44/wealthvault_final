package com.wealthvault.building_api.deletebuilding

import com.wealthvault.building_api.model.DeleteBuildingResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface DeleteBuildingApi {
    @DELETE("ic_nav_asset/building/{id}")
    suspend fun deleteBuilding(@Path("id") id: String): DeleteBuildingResponse
}

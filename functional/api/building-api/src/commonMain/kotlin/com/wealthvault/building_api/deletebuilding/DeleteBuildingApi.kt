package com.wealthvault.building_api.deletebuilding

import com.wealthvault.core.model.DeleteBaseResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface DeleteBuildingApi {
    @DELETE("asset/building/{id}")
    suspend fun deleteBuilding(@Path("id") id: String): DeleteBaseResponse
}

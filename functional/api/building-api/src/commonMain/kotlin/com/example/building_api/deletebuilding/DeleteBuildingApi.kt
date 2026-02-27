package com.example.building_api.deletebuilding

import com.example.building_api.model.DeleteBuildingResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface DeleteBuildingApi {
    @DELETE("asset/building/{id}")
    suspend fun deleteBuilding(@Path("id") id: String): DeleteBuildingResponse
}

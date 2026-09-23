package com.wealthvault.building_api.deletebuilding

interface DeleteBuildingApi {
    suspend fun deleteBuilding(id: String)
}

package com.wealthvault.data.portfolio.building.transport.deletebuilding

interface DeleteBuildingApi {
    suspend fun deleteBuilding(id: String)
}

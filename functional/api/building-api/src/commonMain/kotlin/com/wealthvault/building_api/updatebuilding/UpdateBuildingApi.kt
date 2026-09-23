package com.wealthvault.building_api.updatebuilding

import com.wealthvault.domain.portfolio.BuildingData
import com.wealthvault.domain.portfolio.BuildingRequest

interface UpdateBuildingApi {
    suspend fun updateBuilding(id: String, request: BuildingRequest): BuildingData
}

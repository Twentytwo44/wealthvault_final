package com.wealthvault.building_api.getbuilding

import com.wealthvault.domain.portfolio.GetBuildingData

interface GetBuildingApi {
    suspend fun getBuilding(): List<GetBuildingData>
}

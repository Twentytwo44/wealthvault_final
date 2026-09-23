package com.wealthvault.data.portfolio.building.transport.getbuilding

import com.wealthvault.domain.portfolio.GetBuildingData

interface GetBuildingApi {
    suspend fun getBuilding(): List<GetBuildingData>
}

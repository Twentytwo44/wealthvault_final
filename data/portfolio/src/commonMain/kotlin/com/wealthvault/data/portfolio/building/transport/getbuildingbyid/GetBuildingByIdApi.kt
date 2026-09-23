package com.wealthvault.data.portfolio.building.transport.getbuildingbyid

import com.wealthvault.domain.portfolio.BuildingIdData

interface GetBuildingByIdApi {
    suspend fun getBuildingById(id: String): BuildingIdData?
}

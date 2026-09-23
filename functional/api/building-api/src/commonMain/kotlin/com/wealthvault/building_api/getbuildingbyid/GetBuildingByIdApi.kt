package com.wealthvault.building_api.getbuildingbyid

import com.wealthvault.domain.portfolio.BuildingIdData

interface GetBuildingByIdApi {
    suspend fun getBuildingById(id: String): BuildingIdData?
}

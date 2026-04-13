package com.wealthvault_final.`financial-asset`.data.building

import com.wealthvault.building_api.model.BuildingData
import com.wealthvault.building_api.model.BuildingRequest
import com.wealthvault.investment_api.createcash.CreateBuildingApi

class BuildingNetworkDataSource(
    private val createBuildingApi: CreateBuildingApi,
) {
    suspend fun createBuilding(request: BuildingRequest): Result<BuildingData> {
        return runCatching {
            val result = createBuildingApi.create(request)
            print("result create building"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}

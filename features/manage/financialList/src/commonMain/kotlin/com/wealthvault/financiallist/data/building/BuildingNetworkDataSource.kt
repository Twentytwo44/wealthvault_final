package com.wealthvault.financiallist.data.building

import com.wealthvault.building_api.model.BuildingData
import com.wealthvault.building_api.model.BuildingRequest
import com.wealthvault.building_api.updatebuilding.UpdateBuildingApi

class BuildingNetworkDataSource(
    private val updateBuildingApi: UpdateBuildingApi,
) {
    suspend fun updateBuilding(id: String,request: BuildingRequest): Result<BuildingData> {
        return runCatching {
            val result = updateBuildingApi.updateBuilding(id,request)
            print("Result Building Update:"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}

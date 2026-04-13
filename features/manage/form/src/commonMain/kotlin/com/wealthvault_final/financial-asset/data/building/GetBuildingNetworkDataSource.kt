package com.wealthvault_final.`financial-asset`.data.building

import com.wealthvault.building_api.getbuilding.GetBuildingApi
import com.wealthvault.building_api.model.GetBuildingData


class GetBuildingNetworkDataSource(
    private val getBuildingApi: GetBuildingApi,
) {
    suspend fun getBuilding(): Result<List<GetBuildingData>> {
        return runCatching {
            val result = getBuildingApi.getBuilding()
            print("result"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}

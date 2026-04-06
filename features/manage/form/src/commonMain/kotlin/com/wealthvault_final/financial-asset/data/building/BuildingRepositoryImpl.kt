package com.wealthvault_final.`financial-asset`.data.building

import com.wealthvault.building_api.model.BuildingData
import com.wealthvault.building_api.model.BuildingRequest


class BuildingRepositoryImpl(
    private val networkDataSource: BuildingNetworkDataSource,
) {
    suspend fun createBuilding(request: BuildingRequest): Result<BuildingData> {
        return networkDataSource.createBuilding(request).map { data ->
                data
        }
    }

}

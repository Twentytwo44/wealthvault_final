package com.wealthvault.financiallist.data.building

import com.wealthvault.building_api.model.BuildingData
import com.wealthvault.building_api.model.BuildingRequest


class BuildingRepositoryImpl(
    private val networkDataSource: BuildingNetworkDataSource,
) {
    suspend fun updateBuilding(id:String,request: BuildingRequest): Result<BuildingData> {
        return networkDataSource.updateBuilding(id,request).map { data ->
                data
        }
    }

}

package com.wealthvault_final.`financial-asset`.data.building


import com.wealthvault.building_api.model.GetBuildingData

class GetBuildingRepositoryImpl(
    private val networkDataSource: GetBuildingNetworkDataSource,
) {
    suspend fun getBuilding(): Result<List<GetBuildingData>> {
        return networkDataSource.getBuilding().map { data ->
                data
        }
    }

}

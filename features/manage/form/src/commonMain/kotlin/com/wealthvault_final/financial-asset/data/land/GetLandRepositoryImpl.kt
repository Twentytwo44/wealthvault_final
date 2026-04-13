package com.wealthvault_final.`financial-asset`.data.land


import com.wealthvault.land_api.model.GetLandData

class GetLandRepositoryImpl(
    private val networkDataSource: GetLandNetworkDataSource,
) {
    suspend fun getLand(): Result<List<GetLandData>> {
        return networkDataSource.getLand().map { data ->
                data
        }
    }

}

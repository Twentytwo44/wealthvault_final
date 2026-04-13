package com.wealthvault_final.`financial-asset`.data.land


import com.wealthvault.land_api.model.LandData
import com.wealthvault.land_api.model.LandRequest

class LandRepositoryImpl(
    private val networkDataSource: LandNetworkDataSource,
) {
    suspend fun create(request: LandRequest): Result<LandData> {
        return networkDataSource.create(request).map { data ->
                data
        }
    }

}

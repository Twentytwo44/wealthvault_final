package com.wealthvault.financiallist.data.land


import com.wealthvault.land_api.model.LandData
import com.wealthvault.land_api.model.LandRequest

class LandRepositoryImpl(
    private val networkDataSource: LandNetworkDataSource,
) {
    suspend fun updateLand(id:String,request: LandRequest): Result<LandData> {
        return networkDataSource.updateLand(id,request).map { data ->
                data
        }
    }

}

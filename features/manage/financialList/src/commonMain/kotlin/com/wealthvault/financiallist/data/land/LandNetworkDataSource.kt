package com.wealthvault.financiallist.data.land

import com.wealthvault.land_api.model.LandData
import com.wealthvault.land_api.model.LandRequest
import com.wealthvault.land_api.updateland.UpdateLandApi


class LandNetworkDataSource(
    private val updateLandApi: UpdateLandApi,
) {
    suspend fun updateLand(id:String,request: LandRequest): Result<LandData> {
        return runCatching {
            val result = updateLandApi.updateLand(id,request)
            print("Result Land Update:"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}

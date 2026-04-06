package com.wealthvault_final.`financial-asset`.data.land

import com.wealthvault.investment_api.createcash.CreateLandApi
import com.wealthvault.land_api.model.LandData
import com.wealthvault.land_api.model.LandRequest


class LandNetworkDataSource(
    private val createLandApi: CreateLandApi,
) {
    suspend fun create(request: LandRequest): Result<LandData> {
        return runCatching {
            val result = createLandApi.create(request)
            print("result"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}

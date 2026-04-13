package com.wealthvault_final.`financial-asset`.data.land

import com.wealthvault.land_api.getland.GetLandApi
import com.wealthvault.land_api.model.GetLandData


class GetLandNetworkDataSource(
    private val getLandApi: GetLandApi,
) {
    suspend fun getLand(): Result<List<GetLandData>> {
        return runCatching {
            val result = getLandApi.getLand()
            print("result"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}

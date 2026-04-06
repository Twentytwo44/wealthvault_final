package com.wealthvault_final.`financial-obligations`.data.liability

import com.wealthvault.investment_api.createcash.CreateLiabilityApi
import com.wealthvault.liability_api.model.LiabilityData
import com.wealthvault.liability_api.model.LiabilityRequest

class LiabilityNetworkDataSource(
    private val createLiabilityApi: CreateLiabilityApi,
) {
    suspend fun createLiability(request: LiabilityRequest): Result<LiabilityData> {
        return runCatching {
            val result = createLiabilityApi.create(request)
            print("result"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}

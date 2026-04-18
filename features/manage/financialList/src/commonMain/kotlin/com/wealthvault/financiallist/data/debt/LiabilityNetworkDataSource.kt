package com.wealthvault.financiallist.data.debt

import com.wealthvault.liability_api.model.LiabilityData
import com.wealthvault.liability_api.model.LiabilityRequest
import com.wealthvault.liability_api.updateliability.UpdateLiabilityApi

class LiabilityNetworkDataSource(
    private val createLiabilityApi: UpdateLiabilityApi,
) {
    suspend fun updateLiability(id:String,request: LiabilityRequest): Result<LiabilityData> {
        return runCatching {
            val result = createLiabilityApi.updateLiability(id,request)
            print("Result Liability Update:"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}

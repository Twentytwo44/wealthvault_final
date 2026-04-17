package com.wealthvault.financiallist.data.debt

import com.wealthvault.liability_api.model.LiabilityData
import com.wealthvault.liability_api.model.LiabilityRequest

class LiabilityRepositoryImpl(
    private val networkDataSource: LiabilityNetworkDataSource,
) {
    suspend fun updateLiability(id:String,request: LiabilityRequest): Result<LiabilityData> {
        return networkDataSource.updateLiability(id,request).map { data ->
                data
        }
    }

}

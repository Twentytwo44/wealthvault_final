package com.wealthvault_final.`financial-obligations`.data.liability

import com.wealthvault.liability_api.model.LiabilityData
import com.wealthvault.liability_api.model.LiabilityRequest

class LiabilityRepositoryImpl(
    private val networkDataSource: LiabilityNetworkDataSource,
) {
    suspend fun createLiability(request: LiabilityRequest): Result<LiabilityData> {
        return networkDataSource.createLiability(request).map { data ->
                data
        }
    }

}

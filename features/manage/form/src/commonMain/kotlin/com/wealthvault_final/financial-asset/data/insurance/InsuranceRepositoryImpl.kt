package com.wealthvault_final.`financial-asset`.data.insurance

import com.wealthvault.insurance_api.model.InsuranceData
import com.wealthvault.insurance_api.model.InsuranceRequest

class InsuranceRepositoryImpl(
    private val networkDataSource: InsuranceNetworkDataSource,
) {
    suspend fun createInsurance(request: InsuranceRequest): Result<InsuranceData> {
        return networkDataSource.createInsurance(request).map { data ->
                data
        }
    }

}

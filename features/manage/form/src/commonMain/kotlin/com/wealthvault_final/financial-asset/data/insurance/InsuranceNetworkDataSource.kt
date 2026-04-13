package com.wealthvault_final.`financial-asset`.data.insurance

import com.wealthvault.insurance_api.createcash.CreateInsuranceApi
import com.wealthvault.insurance_api.model.InsuranceData
import com.wealthvault.insurance_api.model.InsuranceRequest

class InsuranceNetworkDataSource(
    private val createInsurancApi: CreateInsuranceApi,
) {
    suspend fun createInsurance(request: InsuranceRequest): Result<InsuranceData> {
        return runCatching {
            val result = createInsurancApi.create(request)
            print("result"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}

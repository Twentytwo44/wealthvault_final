package com.wealthvault_final.`financial-asset`.data.insurance

import com.wealthvault.insurance_api.getinsurance.GetInsuranceApi
import com.wealthvault.insurance_api.model.GetInsuranceData

class GetInsuranceNetworkDataSource(
    private val getInsuranceApi: GetInsuranceApi,
) {
    suspend fun getInsurance(): Result<List<GetInsuranceData>> {
        return runCatching {
            val result = getInsuranceApi.getInsurance()
            print("result"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}

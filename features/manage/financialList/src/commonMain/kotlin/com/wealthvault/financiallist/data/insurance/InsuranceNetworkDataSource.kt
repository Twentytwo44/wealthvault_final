package com.wealthvault.financiallist.data.insurance

import com.wealthvault.insurance_api.model.InsuranceData
import com.wealthvault.insurance_api.model.InsuranceRequest
import com.wealthvault.insurance_api.updateinsurance.UpdateInsuranceApi

class InsuranceNetworkDataSource(
    private val updateInsurancApi: UpdateInsuranceApi,
) {
    suspend fun updateInsurance(id:String,request: InsuranceRequest): Result<InsuranceData> {
        return runCatching {
            val result = updateInsurancApi.updateInsurance(id,request)
            print("Result Insurance Update:"+ result)
            result.data ?: throw IllegalArgumentException(result.error)
        }
    }
}

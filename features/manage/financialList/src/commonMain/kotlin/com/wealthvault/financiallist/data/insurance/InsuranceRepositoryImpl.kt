package com.wealthvault.financiallist.data.insurance

import com.wealthvault.insurance_api.model.InsuranceData
import com.wealthvault.insurance_api.model.InsuranceRequest

class InsuranceRepositoryImpl(
    private val networkDataSource: InsuranceNetworkDataSource,
) {
    suspend fun updateInsurance(id:String,request: InsuranceRequest): Result<InsuranceData> {
        return networkDataSource.updateInsurance(id,request).map { data ->
                data
        }
    }

}

package com.wealthvault_final.`financial-asset`.data.insurance

import com.wealthvault.insurance_api.model.GetInsuranceData


class GetInsuranceRepositoryImpl(
    private val networkDataSource: GetInsuranceNetworkDataSource,
) {
    suspend fun getInsurance(): Result<List<GetInsuranceData>> {
        return networkDataSource.getInsurance().map { data ->
                data
        }
    }

}

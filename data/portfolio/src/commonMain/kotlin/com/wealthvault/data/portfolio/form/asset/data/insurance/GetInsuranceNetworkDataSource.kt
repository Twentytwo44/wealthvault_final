package com.wealthvault.data.portfolio.form.asset.data.insurance

import com.wealthvault.data.portfolio.insurance.transport.getinsurance.GetInsuranceApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.domain.portfolio.GetInsuranceData

class GetInsuranceNetworkDataSource(
    private val getInsuranceApi: GetInsuranceApi,
) {
    suspend fun getInsurance(): AppResult<List<GetInsuranceData>> {
        return runSuspendAppCatching {
            getInsuranceApi.getInsurance()
        }
    }
}

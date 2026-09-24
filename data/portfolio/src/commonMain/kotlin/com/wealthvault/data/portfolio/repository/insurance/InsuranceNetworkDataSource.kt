package com.wealthvault.data.portfolio.repository.insurance

import com.wealthvault.domain.portfolio.InsuranceRequest
import com.wealthvault.data.portfolio.insurance.transport.updateinsurance.UpdateInsuranceApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.domain.portfolio.InsuranceData

class InsuranceNetworkDataSource(
    private val updateInsurancApi: UpdateInsuranceApi,
) {
    suspend fun updateInsurance(id: String, request: InsuranceRequest): AppResult<InsuranceData> {
        return runSuspendAppCatching {
            updateInsurancApi.updateInsurance(id, request)
        }
    }
}

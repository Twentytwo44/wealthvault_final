package com.wealthvault.data.portfolio.form.asset.data.insurance

import com.wealthvault.data.portfolio.insurance.transport.createcash.CreateInsuranceApi
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.domain.portfolio.InsuranceData
import com.wealthvault.domain.portfolio.InsuranceRequest

class InsuranceNetworkDataSource(
    private val createInsurancApi: CreateInsuranceApi,
) {
    suspend fun createInsurance(request: InsuranceRequest): AppResult<InsuranceData> {
        return runSuspendAppCatching {
            createInsurancApi.create(request)
        }
    }
}

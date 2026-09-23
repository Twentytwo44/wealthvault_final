package com.wealthvault.data.portfolio.insurance.transport.updateinsurance

import com.wealthvault.domain.portfolio.InsuranceData
import com.wealthvault.domain.portfolio.InsuranceRequest

interface UpdateInsuranceApi {
    suspend fun updateInsurance(id: String, request: InsuranceRequest): InsuranceData
}

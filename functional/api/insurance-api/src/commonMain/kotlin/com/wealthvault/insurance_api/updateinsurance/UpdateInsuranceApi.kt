package com.wealthvault.insurance_api.updateinsurance

import com.wealthvault.domain.portfolio.InsuranceData
import com.wealthvault.domain.portfolio.InsuranceRequest

interface UpdateInsuranceApi {
    suspend fun updateInsurance(id: String, request: InsuranceRequest): InsuranceData
}

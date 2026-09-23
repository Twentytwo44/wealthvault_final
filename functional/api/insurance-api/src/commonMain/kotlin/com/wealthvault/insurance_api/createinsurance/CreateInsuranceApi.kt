package com.wealthvault.insurance_api.createcash

import com.wealthvault.domain.portfolio.InsuranceData
import com.wealthvault.domain.portfolio.InsuranceRequest

interface CreateInsuranceApi {
    suspend fun create(request: InsuranceRequest): InsuranceData
}

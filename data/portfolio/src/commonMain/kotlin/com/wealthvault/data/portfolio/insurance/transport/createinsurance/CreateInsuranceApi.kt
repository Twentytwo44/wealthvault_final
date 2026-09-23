package com.wealthvault.data.portfolio.insurance.transport.createcash

import com.wealthvault.domain.portfolio.InsuranceData
import com.wealthvault.domain.portfolio.InsuranceRequest

interface CreateInsuranceApi {
    suspend fun create(request: InsuranceRequest): InsuranceData
}

package com.wealthvault.data.portfolio.insurance.transport.getinsurance

import com.wealthvault.domain.portfolio.GetInsuranceData

interface GetInsuranceApi {
    suspend fun getInsurance(): List<GetInsuranceData>
}

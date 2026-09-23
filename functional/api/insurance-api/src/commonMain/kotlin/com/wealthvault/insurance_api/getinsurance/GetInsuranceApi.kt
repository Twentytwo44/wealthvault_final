package com.wealthvault.insurance_api.getinsurance

import com.wealthvault.domain.portfolio.GetInsuranceData

interface GetInsuranceApi {
    suspend fun getInsurance(): List<GetInsuranceData>
}

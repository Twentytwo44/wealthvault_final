package com.wealthvault.insurance_api.getinsurancetbyid

import com.wealthvault.domain.portfolio.InsuranceIdData

interface GetInsuranceByIdApi {
    suspend fun getInsuranceById(id: String): InsuranceIdData?
}

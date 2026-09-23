package com.wealthvault.data.portfolio.insurance.transport.getinsurancetbyid

import com.wealthvault.domain.portfolio.InsuranceIdData

interface GetInsuranceByIdApi {
    suspend fun getInsuranceById(id: String): InsuranceIdData?
}

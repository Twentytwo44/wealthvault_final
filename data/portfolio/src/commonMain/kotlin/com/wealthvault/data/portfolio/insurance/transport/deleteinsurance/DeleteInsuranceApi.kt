package com.wealthvault.data.portfolio.insurance.transport.deleteinsurance

interface DeleteInsuranceApi {
    suspend fun deleteInsurance(id: String)
}

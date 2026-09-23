package com.wealthvault.insurance_api.deleteinsurance

interface DeleteInsuranceApi {
    suspend fun deleteInsurance(id: String)
}

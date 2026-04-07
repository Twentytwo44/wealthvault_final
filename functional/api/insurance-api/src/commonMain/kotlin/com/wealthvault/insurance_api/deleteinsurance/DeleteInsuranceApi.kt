package com.wealthvault.insurance_api.deleteinsurance

import com.wealthvault.insurance_api.model.DeleteInsuranceResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface DeleteInsuranceApi {
    @DELETE("asset/insurance/{id}/")
    suspend fun deleteInsurance(@Path("id") id: String): DeleteInsuranceResponse
}

package com.wealthvault.liability_api.deleteliability

import com.wealthvault.core.model.DeleteBaseResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface DeleteLiabilityApi {
    @DELETE("lia/{id}/")
    suspend fun deleteLiability(@Path("id") id: String): DeleteLiabilityResponse
}

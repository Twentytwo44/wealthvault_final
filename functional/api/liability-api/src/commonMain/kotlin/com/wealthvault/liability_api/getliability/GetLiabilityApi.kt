package com.wealthvault.liability_api.getliability

import com.wealthvault.liability_api.model.GetLiabilityResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetLiabilityApi {
    @GET("lia")
    suspend fun getLiability(): GetLiabilityResponse
}

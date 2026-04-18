package com.wealthvault.investment_api.createcash


import com.wealthvault.liability_api.model.LiabilityRequest
import com.wealthvault.liability_api.model.LiabilityResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface CreateLiabilityApi {
    @POST("lia/")
    suspend fun create(@Body request: LiabilityRequest): LiabilityResponse
}

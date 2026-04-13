package com.wealthvault.investment_api.createcash


import com.wealthvault.land_api.model.LandRequest
import com.wealthvault.land_api.model.LandResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface CreateLandApi {
    @POST("asset/land")
    suspend fun create(@Body request: LandRequest): LandResponse
}

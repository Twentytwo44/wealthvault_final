package com.example.liability_api.getliability

import com.example.liability_api.model.GetLiabilityResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetLiabilityApi {
    @GET("ic_nav_asset/lia")
    suspend fun getLiability(): GetLiabilityResponse
}

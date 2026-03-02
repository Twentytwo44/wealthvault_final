package com.example.investment_api.createcash


import com.example.liability_api.model.LiabilityRequest
import com.example.liability_api.model.LiabilityResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface CreateLiabilityApi {
    @POST("asset/lia")
    suspend fun create(@Body request: LiabilityRequest): LiabilityResponse
}

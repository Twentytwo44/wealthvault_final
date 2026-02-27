package com.example.liability_api.getliabilitybyid

import com.example.liability_api.model.LiabilityIdResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetLiabilityByIdApi {
    @GET("asset/lia/{id}")
    suspend fun getLiabilityById(@Path("id") id: String): LiabilityIdResponse
}

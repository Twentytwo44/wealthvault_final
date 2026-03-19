package com.wealthvault.liability_api.getliabilitybyid

import com.wealthvault.liability_api.model.LiabilityIdResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetLiabilityByIdApi {
    @GET("ic_nav_asset/lia/{id}")
    suspend fun getLiabilityById(@Path("id") id: String): LiabilityIdResponse
}

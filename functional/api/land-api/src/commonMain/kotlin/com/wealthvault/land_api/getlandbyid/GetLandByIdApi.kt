package com.wealthvault.land_api.getlandbyid

import com.wealthvault.land_api.model.LandIdResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetLandByIdApi {
    @GET("asset/land/{id}/")
    suspend fun getLandById(@Path("id") id: String): LandIdResponse
}

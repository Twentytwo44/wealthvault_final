package com.wealthvault.land_api.getland

import com.wealthvault.land_api.model.GetLandResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetLandApi {
    @GET("asset/land")
    suspend fun getLand(): GetLandResponse
}

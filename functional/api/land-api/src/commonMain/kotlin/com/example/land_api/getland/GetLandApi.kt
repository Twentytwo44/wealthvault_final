package com.example.land_api.getland

import com.example.land_api.model.GetLandResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetLandApi {
    @GET("ic_nav_asset/land")
    suspend fun getLand(): GetLandResponse
}

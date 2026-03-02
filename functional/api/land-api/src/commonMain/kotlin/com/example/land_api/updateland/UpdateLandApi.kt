package com.example.land_api.updateland

import com.example.land_api.model.LandRequest
import com.example.land_api.model.LandResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.Path

interface UpdateLandApi {
    @PATCH("asset/land/{id}")
    suspend fun updateLand(@Path("id") id: String, @Body request: LandRequest): LandResponse
}

package com.example.land_api.deleteland

import com.example.land_api.model.DeleteLandResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface DeleteLandApi {
    @DELETE("ic_nav_asset/land/{id}")
    suspend fun deleteLand(@Path("id") id: String): DeleteLandResponse
}

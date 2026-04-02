package com.wealthvault.land_api.deleteland

import com.wealthvault.core.model.DeleteBaseResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface DeleteLandApi {
    @DELETE("asset/land/{id}")
    suspend fun deleteLand(@Path("id") id: String): DeleteBaseResponse
}

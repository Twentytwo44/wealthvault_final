package com.wealthvault.land_api.deleteland

interface DeleteLandApi {
    suspend fun deleteLand(id: String)
}

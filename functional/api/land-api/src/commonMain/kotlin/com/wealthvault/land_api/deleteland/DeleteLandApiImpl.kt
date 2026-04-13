package com.wealthvault.land_api.deleteland

import com.wealthvault.config.Config
import com.wealthvault.land_api.model.DeleteLandResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.delete


class DeleteLandApiImpl(private val ktorfit: Ktorfit) : DeleteLandApi {

    override suspend fun deleteLand(id: String): DeleteLandResponse {
        val client = ktorfit.httpClient

        return client.delete("${Config.localhost_android}asset/land/$id/") {

        }.body()
    }
}

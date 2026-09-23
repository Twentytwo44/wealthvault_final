package com.wealthvault.land_api.deleteland

import com.wealthvault.config.Config
import com.wealthvault.land_api.model.DeleteLandResponse
import com.wealthvault.land_api.requireSuccess
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete


class DeleteLandApiImpl(private val client: HttpClient) : DeleteLandApi {

    override suspend fun deleteLand(id: String) {
        client.delete("${Config.localhost_android}asset/land/$id/") {

        }.body<DeleteLandResponse>().requireSuccess()
    }
}

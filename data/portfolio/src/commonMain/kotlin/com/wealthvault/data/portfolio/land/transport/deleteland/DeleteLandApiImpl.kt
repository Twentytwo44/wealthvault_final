package com.wealthvault.data.portfolio.land.transport.deleteland

import com.wealthvault.config.Config
import com.wealthvault.data.portfolio.land.transport.model.DeleteLandResponse
import com.wealthvault.data.portfolio.land.transport.requireSuccess
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete


class DeleteLandApiImpl(private val client: HttpClient) : DeleteLandApi {

    override suspend fun deleteLand(id: String) {
        client.delete("${Config.localhost_android}asset/land/$id/") {

        }.body<DeleteLandResponse>().requireSuccess()
    }
}

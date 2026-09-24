package com.wealthvault.data.social.share.transport.unsharegroup


import com.wealthvault.config.Config
import com.wealthvault.data.social.share.transport.model.ShareItemResponse
import com.wealthvault.data.social.share.transport.requireSuccess
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete

class UnShareGroupApiImpl(private val client: HttpClient) : UnShareGroupApi {
    override suspend fun unShareGroup(id:String) {
        client.delete("${Config.localhost_android}group/item/${id}/") {
        }.body<ShareItemResponse>().requireSuccess()
    }
}

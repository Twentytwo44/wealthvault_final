package com.wealthvault.data.social.share.transport.unsharefriend


import com.wealthvault.config.Config
import com.wealthvault.data.social.share.transport.model.ShareItemResponse
import com.wealthvault.data.social.share.transport.requireSuccess
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete

class UnShareFriendApiImpl(private val client: HttpClient) : UnShareFriendApi {
    override suspend fun unShareFriend(id:String) {
        client.delete("${Config.localhost_android}friend/item/${id}/") {
        }.body<ShareItemResponse>().requireSuccess()
    }
}

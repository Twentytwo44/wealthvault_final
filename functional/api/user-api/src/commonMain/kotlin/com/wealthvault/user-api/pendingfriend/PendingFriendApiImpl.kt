package com.wealthvault.`user-api`.pendingfriend

import com.wealthvault.`user-api`.model.PendingFriendResponse
import com.wealthvault.config.Config
import com.wealthvault.user_api.requireDomainPendingFriends
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class PendingFriendApiImpl(private val client: HttpClient) : PendingFriendApi {
    override suspend fun pendingFriend() = client
        .get("${Config.localhost_android}friend/pending")
        .body<PendingFriendResponse>()
        .requireDomainPendingFriends()
}

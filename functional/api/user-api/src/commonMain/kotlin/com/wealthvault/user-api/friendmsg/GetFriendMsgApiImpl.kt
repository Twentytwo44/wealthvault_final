package com.wealthvault.`user-api`.friendmsg

import com.wealthvault.config.Config
import com.wealthvault.`user-api`.model.MessageResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import com.wealthvault.user_api.toDomainMessages

class GetFriendMsgApiImpl(private val client: HttpClient) : GetFriendMsgApi {
    override suspend fun getFriendMsg(id: String) = client
        .get("${Config.localhost_android}friend/${id}/msg/")
        .body<MessageResponse>()
        .toDomainMessages()
}

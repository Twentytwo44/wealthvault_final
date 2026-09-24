package com.wealthvault.data.social.group.transport.groupmsg

import com.wealthvault.config.Config
import com.wealthvault.data.social.group.transport.model.GroupMsgResponse
import com.wealthvault.data.social.group.transport.requireDomainMessages
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetGroupMsgApiImpl(private val client: HttpClient) : GetGroupMsgApi {
    override suspend fun getGroupMsg(id:String) = client
        .get("${Config.localhost_android}group/$id/msg/")
        .body<GroupMsgResponse>()
        .requireDomainMessages()
}

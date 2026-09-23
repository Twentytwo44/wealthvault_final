package com.wealthvault.group_api.groupmsg

import com.wealthvault.config.Config
import com.wealthvault.group_api.model.GroupMsgResponse
import com.wealthvault.group_api.requireDomainMessages
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetGroupMsgApiImpl(private val client: HttpClient) : GetGroupMsgApi {
    override suspend fun getGroupMsg(id:String) = client
        .get("${Config.localhost_android}group/$id/msg/")
        .body<GroupMsgResponse>()
        .requireDomainMessages()
}

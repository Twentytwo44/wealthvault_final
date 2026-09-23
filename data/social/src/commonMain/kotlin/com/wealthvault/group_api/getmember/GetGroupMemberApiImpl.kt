package com.wealthvault.data.social.group.transport.getmember

import com.wealthvault.config.Config
import com.wealthvault.data.social.group.transport.model.GroupMemberResponse
import com.wealthvault.data.social.group.transport.requireDomainMembers
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetGroupMemberApiImpl(private val client: HttpClient) : GetGroupMemberApi {
    override suspend fun getGroupMembers(id: String) = client
        .get("${Config.localhost_android}group/member/$id/") {}
        .body<GroupMemberResponse>()
        .requireDomainMembers()
}

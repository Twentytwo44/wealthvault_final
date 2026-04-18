package com.wealthvault.group_api.getmember

import com.wealthvault.config.Config
import com.wealthvault.group_api.model.GroupMemberResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetGroupMemberApiImpl(private val ktorfit: Ktorfit) : GetGroupMemberApi {
    override suspend fun getGroupMembers(id: String): GroupMemberResponse {
        val client = ktorfit.httpClient
        return client.get("${Config.localhost_android}group/member/${id}/") {}.body()
    }
}

package com.wealthvault.group_api.getgrouplist

import com.wealthvault.config.Config
import com.wealthvault.domain.social.GroupSummary
import com.wealthvault.group_api.model.GetGroupResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetAllGroupApiImpl(private val client: HttpClient) : GetAllGroupApi {
    override suspend fun getAllGroup(): List<GroupSummary> {
        val response: GetGroupResponse = client.get("${Config.localhost_android}group/").body()
        response.error?.let { error -> throw IllegalStateException(error) }
        return response.data.orEmpty().map { item ->
            GroupSummary(
                id = item.id,
                groupName = item.groupName,
                groupProfile = item.groupProfile,
                createdBy = item.createdBy,
                memberCount = item.memberCount,
                createdAt = item.createdAt,
                updatedAt = item.updatedAt,
            )
        }
    }
}

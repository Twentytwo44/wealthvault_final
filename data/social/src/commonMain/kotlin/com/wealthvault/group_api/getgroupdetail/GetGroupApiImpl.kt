package com.wealthvault.data.social.group.transport.getgroupdetail

import com.wealthvault.config.Config
import com.wealthvault.data.social.group.transport.model.GroupResponse
import com.wealthvault.data.social.group.transport.requireDomainResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetGroupApiImpl(private val client: HttpClient) : GetGroupApi {
    override suspend fun getGroupDetail(id: String): com.wealthvault.domain.social.GroupResult { // 🌟 เปลี่ยนชื่อฟังก์ชัน
        return client.get("${Config.localhost_android}group/detail/${id}") {}.body<GroupResponse>().requireDomainResult()
    }
}

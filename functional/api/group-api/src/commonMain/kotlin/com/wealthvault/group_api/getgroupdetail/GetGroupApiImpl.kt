package com.wealthvault.group_api.getgroupdetail

import com.wealthvault.config.Config
import com.wealthvault.group_api.model.GroupResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetGroupApiImpl(private val ktorfit: Ktorfit) : GetGroupApi {
    override suspend fun getGroupDetail(id: String): GroupResponse { // 🌟 เปลี่ยนชื่อฟังก์ชัน
        val client = ktorfit.httpClient
        return client.get("${Config.localhost_android}group/detail/${id}") {}.body()
    }
}

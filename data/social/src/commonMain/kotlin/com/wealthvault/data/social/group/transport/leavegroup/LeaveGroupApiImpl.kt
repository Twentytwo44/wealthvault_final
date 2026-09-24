package com.wealthvault.data.social.group.transport.leavegroup


import com.wealthvault.config.Config
import com.wealthvault.data.social.group.transport.model.grantaccess
import com.wealthvault.data.social.group.transport.requireSuccess
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete

class LeaveGroupApiImpl(private val client: HttpClient) : LeaveGroupApi {
    override suspend fun leaveGroup(id: String): Boolean {
        // 🌟 เปลี่ยนจาก post เป็น delete และใช้ GrantAccessResponse
        return client.delete("${Config.localhost_android}group/${id}/leave") {
        }.body<grantaccess>().requireSuccess()
    }
}

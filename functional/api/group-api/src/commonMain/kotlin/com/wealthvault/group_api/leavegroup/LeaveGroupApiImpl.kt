package com.wealthvault.group_api.leavegroup


import com.wealthvault.config.Config
import com.wealthvault.group_api.model.grantaccess
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.delete

class LeaveGroupApiImpl(private val ktorfit: Ktorfit) : LeaveGroupApi {
    override suspend fun leaveGroup(id: String): grantaccess {
        val client = ktorfit.httpClient

        // 🌟 เปลี่ยนจาก post เป็น delete และใช้ GrantAccessResponse
        return client.delete("${Config.localhost_android}group/${id}/leave") {
        }.body()
    }
}
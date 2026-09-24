package com.wealthvault.data.social.group.transport.deletegroup

import com.wealthvault.config.Config
import com.wealthvault.data.social.group.transport.model.DeleteGroupResponse
import com.wealthvault.data.social.group.transport.requireSuccess
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete

class DeleteGroupApiImpl(private val client: HttpClient) : DeleteGroupApi {
    override suspend fun deleteGroup(id: String): Boolean {
        // 🌟 เรียกใช้ Config.localhost_android ตามแพทเทิร์นเดิมของคุณ Champ
        return client.delete("${Config.localhost_android}group/${id}") {
        }.body<DeleteGroupResponse>().requireSuccess()
    }
}

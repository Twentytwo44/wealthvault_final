package com.wealthvault.group_api.deletegroup

import com.wealthvault.config.Config
import com.wealthvault.group_api.model.DeleteGroupResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.delete

class DeleteGroupApiImpl(private val ktorfit: Ktorfit) : DeleteGroupApi {
    override suspend fun deleteGroup(id: String): DeleteGroupResponse {
        val client = ktorfit.httpClient

        // 🌟 เรียกใช้ Config.localhost_android ตามแพทเทิร์นเดิมของคุณ Champ
        return client.delete("${Config.localhost_android}group/${id}") {
        }.body()
    }
}
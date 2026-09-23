package com.wealthvault.liability_api.deleteliability

import com.wealthvault.config.Config
import com.wealthvault.liability_api.requireSuccess
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete


class DeleteLiabilityApiImpl(private val client: HttpClient) : DeleteLiabilityApi {

    override suspend fun deleteLiability(id: String) {
        client.delete("${Config.localhost_android}lia/$id/") {
            // ปกติ DELETE ไม่ต้องส่ง Body แต่ต้องแนบ Token
            // ซึ่ง HttpClient ตัวนี้มี Auth Plugin ที่เราเซ็ตไว้ใน ApiModule แล้ว
        }.body<com.wealthvault.liability_api.model.DeleteLiabilityResponse>().requireSuccess()
    }
}

package com.wealthvault.liability_api.deleteliability

import com.wealthvault.config.Config
import com.wealthvault.liability_api.model.DeleteLiabilityResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.delete


class DeleteLiabilityApiImpl(private val ktorfit: Ktorfit) : DeleteLiabilityApi {

    override suspend fun deleteLiability(id: String): DeleteBaseResponse {
        val client = ktorfit.httpClient

        return client.delete("${Config.localhost_android}lia/$id/") {
            // ปกติ DELETE ไม่ต้องส่ง Body แต่ต้องแนบ Token
            // ซึ่ง HttpClient ตัวนี้มี Auth Plugin ที่เราเซ็ตไว้ใน ApiModule แล้ว
        }.body()
    }
}

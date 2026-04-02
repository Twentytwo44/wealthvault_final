package com.wealthvault.land_api.deleteland

import com.wealthvault.config.Config
import com.wealthvault.core.model.DeleteBaseResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.delete


class DeleteLandApiImpl(private val ktorfit: Ktorfit) : DeleteLandApi {

    override suspend fun deleteLand(id: String): DeleteBaseResponse {
        val client = ktorfit.httpClient

        return client.delete("${Config.localhost_android}asset/land/$id") {
            // ปกติ DELETE ไม่ต้องส่ง Body แต่ต้องแนบ Token
            // ซึ่ง HttpClient ตัวนี้มี Auth Plugin ที่เราเซ็ตไว้ใน ApiModule แล้ว
        }.body()
    }
}

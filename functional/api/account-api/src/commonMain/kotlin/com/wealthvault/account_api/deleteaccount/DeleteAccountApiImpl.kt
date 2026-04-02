package com.wealthvault.account_api.deleteaccount

import com.wealthvault.config.Config
import com.wealthvault.core.model.DeleteBaseResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.delete

class DeleteAccountApiImpl(private val ktorfit: Ktorfit) : DeleteAccountApi {

    override suspend fun deleteAccount(id: String): DeleteBaseResponse {
        val client = ktorfit.httpClient

        return client.delete("${Config.localhost_android}asset/account/$id") {
            // ปกติ DELETE ไม่ต้องส่ง Body แต่ต้องแนบ Token
            // ซึ่ง HttpClient ตัวนี้มี Auth Plugin ที่เราเซ็ตไว้ใน ApiModule แล้ว
        }.body()
    }
}

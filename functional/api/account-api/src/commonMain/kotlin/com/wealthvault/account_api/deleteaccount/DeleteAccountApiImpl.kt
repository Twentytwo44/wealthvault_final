package com.wealthvault.account_api.deleteaccount

import com.wealthvault.account_api.requireSuccess
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete

class DeleteAccountApiImpl(private val client: HttpClient) : DeleteAccountApi {

    override suspend fun deleteAccount(id: String) {
        client.delete("${Config.localhost_android}asset/account/$id/") {
            // ปกติ DELETE ไม่ต้องส่ง Body แต่ต้องแนบ Token
            // ซึ่ง HttpClient ตัวนี้มี Auth Plugin ที่เราเซ็ตไว้ใน ApiModule แล้ว
        }.body<com.wealthvault.account_api.model.DeleteAccountResponse>().requireSuccess()
    }
}

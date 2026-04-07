package com.wealthvault.account_api.getaccount

import com.wealthvault.account_api.model.AccountResponse // 🌟 นำเข้าตัวใหม่
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetAccountApiImpl(private val ktorfit: Ktorfit) : GetAccountApi {
    override suspend fun getAccount(): AccountResponse { // 🌟 เปลี่ยนเป็น AccountResponse
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}asset/account/") {

        }.body()
    }
}
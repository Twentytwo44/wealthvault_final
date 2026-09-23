package com.wealthvault.account_api.getaccount

import com.wealthvault.account_api.requireDomainData
import com.wealthvault.config.Config
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.HttpClient

class GetAccountApiImpl(private val client: HttpClient) : GetAccountApi {
    override suspend fun getAccount() = client
        .get("${Config.localhost_android}asset/account/")
        .body<com.wealthvault.account_api.model.AccountResponse>()
        .requireDomainData()
}

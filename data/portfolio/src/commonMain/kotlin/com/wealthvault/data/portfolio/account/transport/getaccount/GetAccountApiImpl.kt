package com.wealthvault.data.portfolio.account.transport.getaccount

import com.wealthvault.data.portfolio.account.transport.requireDomainData
import com.wealthvault.config.Config
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.HttpClient

class GetAccountApiImpl(private val client: HttpClient) : GetAccountApi {
    override suspend fun getAccount() = client
        .get("${Config.localhost_android}asset/account/")
        .body<com.wealthvault.data.portfolio.account.transport.model.AccountResponse>()
        .requireDomainData()
}

package com.wealthvault.data.portfolio.account.transport.getaccountbyid

import com.wealthvault.data.portfolio.account.transport.toDomainData
import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetAccountByIdApiImpl(private val client: HttpClient) : GetAccountByIdApi {
    override suspend fun getAccountById(id: String) = client
        .get("${Config.localhost_android}asset/account/$id/")
        .body<com.wealthvault.data.portfolio.account.transport.model.BankAccountResponse>()
        .toDomainData()
}

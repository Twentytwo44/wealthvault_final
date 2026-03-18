package com.wealthvault.account_api.getaccount

import com.wealthvault.account_api.model.BankAccountResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetAccountApiImpl(private val ktorfit: Ktorfit) : GetAccountApi {
    override suspend fun getAccount(): BankAccountResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}/ic_nav_asset/account") {

        }.body()
    }
}

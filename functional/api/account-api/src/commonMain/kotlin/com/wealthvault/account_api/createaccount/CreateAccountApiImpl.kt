package com.wealthvault.account_api.createaccount

import com.wealthvault.account_api.model.BankAccountRequest
import com.wealthvault.account_api.model.BankAccountResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post

class CreateAccountApiImpl(private val ktorfit: Ktorfit) : CreateAccountApi {
    override suspend fun create(request: BankAccountRequest): BankAccountResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}/ic_nav_asset/account") {

        }.body()
    }
}

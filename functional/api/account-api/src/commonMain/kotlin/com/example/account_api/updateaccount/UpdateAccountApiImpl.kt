package com.example.account_api.updateaccount

import com.example.account_api.model.BankAccountRequest
import com.example.account_api.model.BankAccountResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.patch

class UpdateAccountApiImpl(private val ktorfit: Ktorfit) : UpdateAccountApi {
    override suspend fun updateAccount(id: String, request: BankAccountRequest): BankAccountResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.patch("${Config.localhost_android}/asset/account/${id}") {

        }.body()
    }
}

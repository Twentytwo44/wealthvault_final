package com.example.account_api.getaccountbyid

import com.example.account_api.model.BankAccountResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetAccountByIdApiImpl(private val ktorfit: Ktorfit) : GetAccountByIdApi {
    override suspend fun getAccountById(id: String): BankAccountResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}/asset/account/${id}") {

        }.body()
    }
}

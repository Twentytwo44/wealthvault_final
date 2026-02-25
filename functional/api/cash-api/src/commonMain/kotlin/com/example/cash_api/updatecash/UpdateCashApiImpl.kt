package com.example.cash_api.updatecash

import com.example.account_api.model.BankAccountRequest
import com.example.account_api.model.BankAccountResponse
import com.example.cash_api.model.CashRequest
import com.example.cash_api.model.CashResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.patch

class UpdateCashApiImpl(private val ktorfit: Ktorfit) : UpdateCashApi {
    override suspend fun updateAccount(id: String, request: CashRequest): CashResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.patch("${Config.localhost_android}/asset/cash/${id}") {

        }.body()
    }
}

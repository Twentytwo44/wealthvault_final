package com.example.cash_api.createcash

import com.example.cash_api.model.CashRequest
import com.example.cash_api.model.CashResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post

class CreateCashApiImpl(private val ktorfit: Ktorfit) : CreateCashApi {
    override suspend fun create(request: CashRequest): CashResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}/asset/cash") {

        }.body()
    }
}

package com.example.cash_api.getcashtbyid

import com.example.cash_api.model.CashIdResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetCashByIdApiImpl(private val ktorfit: Ktorfit) : GetCashByIdApi {
    override suspend fun getCashById(id: String): CashIdResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}/asset/cash/${id}") {

        }.body()
    }
}

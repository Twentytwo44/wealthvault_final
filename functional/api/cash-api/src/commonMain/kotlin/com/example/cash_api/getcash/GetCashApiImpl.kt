package com.example.cash_api.getcash

import com.example.cash_api.model.GetCashResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetCashApiImpl(private val ktorfit: Ktorfit) : GetCashApi {
    override suspend fun getCash(): GetCashResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}/asset/cash") {

        }.body()
    }
}

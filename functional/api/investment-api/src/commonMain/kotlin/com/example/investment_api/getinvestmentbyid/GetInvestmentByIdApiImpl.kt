package com.example.investment_api.getinvestmentbyid

import com.example.investment_api.model.InvestmentIdResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetInvestmentByIdApiImpl(private val ktorfit: Ktorfit) : GetInvestmentByIdApi {
    override suspend fun getInvestmentById(id: String): InvestmentIdResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}/asset/invest/${id}") {

        }.body()
    }
}

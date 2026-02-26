package com.example.investment_api.createcash


import com.example.investment_api.model.InvestmentRequest
import com.example.investment_api.model.InvestmentResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post

class CreateInvestmentApiImpl(private val ktorfit: Ktorfit) : CreateInvestmentApi {
    override suspend fun create(request: InvestmentRequest): InvestmentResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}/asset/invest") {

        }.body()
    }
}

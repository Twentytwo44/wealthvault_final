package com.example.investment_api.updateinvestment


import com.example.investment_api.model.InvestmentRequest
import com.example.investment_api.model.InvestmentResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.patch

class UpdateInvestmentApiImpl(private val ktorfit: Ktorfit) : UpdateInvestmentApi {
    override suspend fun updateInvestment(id: String, request: InvestmentRequest): InvestmentResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.patch("${Config.localhost_android}/asset/invest/${id}") {


        }.body()
    }
}

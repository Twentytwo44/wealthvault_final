package com.wealthvault.investment_api.getinvestment

import com.wealthvault.config.Config
import com.wealthvault.investment_api.model.GetInvestmentResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetInvestmentApiImpl(private val ktorfit: Ktorfit) : GetInvestmentApi {
    override suspend fun getInvestment(): GetInvestmentResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}asset/invest") {

        }.body()
    }
}

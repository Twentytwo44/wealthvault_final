package com.wealthvault.data.portfolio.investment.transport.deleteinvestment

import com.wealthvault.config.Config
import com.wealthvault.data.portfolio.investment.transport.requireSuccess
import com.wealthvault.data.portfolio.investment.transport.model.DeleteInvestmentResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete

class DeleteInvestmentApiImpl(private val client: HttpClient) : DeleteInvestmentApi {

    override suspend fun deleteInvestment(id: String) {
        client.delete("${Config.apiBaseUrl}asset/invest/$id/") {
            // ปกติ DELETE ไม่ต้องส่ง Body แต่ต้องแนบ Token
            // ซึ่ง HttpClient ตัวนี้มี Auth Plugin ที่เราเซ็ตไว้ใน ApiModule แล้ว
        }.body<com.wealthvault.data.portfolio.investment.transport.model.DeleteInvestmentResponse>().requireSuccess()
    }
}

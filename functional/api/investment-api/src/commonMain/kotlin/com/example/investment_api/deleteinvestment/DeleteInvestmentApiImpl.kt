package com.example.investment_api.deleteinvestment

import com.example.investment_api.model.DeleteInvestmentResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.delete

class DeleteInvestmentApiImpl(private val ktorfit: Ktorfit) : DeleteInvestmentApi {

    override suspend fun deleteInvestmen(id: String): DeleteInvestmentResponse {
        val client = ktorfit.httpClient

        return client.delete("${Config.localhost_android}/asset/invest/$id") {
            // ปกติ DELETE ไม่ต้องส่ง Body แต่ต้องแนบ Token
            // ซึ่ง HttpClient ตัวนี้มี Auth Plugin ที่เราเซ็ตไว้ใน ApiModule แล้ว
        }.body()
    }
}

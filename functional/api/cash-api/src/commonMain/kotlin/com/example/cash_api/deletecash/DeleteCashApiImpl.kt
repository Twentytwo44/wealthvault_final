package com.example.cash_api.deletecash

import com.example.cash_api.model.DeleteCashResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.delete

class DeleteCashApiImpl(private val ktorfit: Ktorfit) : DeleteCashApi {

    override suspend fun deleteCash(id: String): DeleteCashResponse {
        val client = ktorfit.httpClient

        return client.delete("${Config.localhost_android}/asset/cash/$id") {
            // ปกติ DELETE ไม่ต้องส่ง Body แต่ต้องแนบ Token
            // ซึ่ง HttpClient ตัวนี้มี Auth Plugin ที่เราเซ็ตไว้ใน ApiModule แล้ว
        }.body()
    }
}

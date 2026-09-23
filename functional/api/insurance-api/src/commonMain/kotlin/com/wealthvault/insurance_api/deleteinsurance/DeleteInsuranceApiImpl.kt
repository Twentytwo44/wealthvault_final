package com.wealthvault.insurance_api.deleteinsurance

import com.wealthvault.config.Config
import com.wealthvault.insurance_api.requireSuccess
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete

class DeleteInsuranceApiImpl(private val client: HttpClient) : DeleteInsuranceApi {

    override suspend fun deleteInsurance(id: String) {
        client.delete("${Config.localhost_android}asset/insurance/$id/") {
            // ปกติ DELETE ไม่ต้องส่ง Body แต่ต้องแนบ Token
            // ซึ่ง HttpClient ตัวนี้มี Auth Plugin ที่เราเซ็ตไว้ใน ApiModule แล้ว
        }.body<com.wealthvault.insurance_api.model.DeleteInsuranceResponse>().requireSuccess()
    }
}

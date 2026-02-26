package com.example.insurance_api.deleteinsurance

import com.example.insurance_api.model.DeleteInsuranceResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.delete

class DeleteInsuranceApiImpl(private val ktorfit: Ktorfit) : DeleteInsuranceApi {

    override suspend fun deleteInsurance(id: String): DeleteInsuranceResponse {
        val client = ktorfit.httpClient

        return client.delete("${Config.localhost_android}/asset/insurance/$id") {
            // ปกติ DELETE ไม่ต้องส่ง Body แต่ต้องแนบ Token
            // ซึ่ง HttpClient ตัวนี้มี Auth Plugin ที่เราเซ็ตไว้ใน ApiModule แล้ว
        }.body()
    }
}

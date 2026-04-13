package com.wealthvault.land_api.getland

import com.wealthvault.config.Config
import com.wealthvault.land_api.model.GetLandResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetLandApiImpl(private val ktorfit: Ktorfit) : GetLandApi {
    override suspend fun getLand(): GetLandResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}asset/land/") {

        }.body()
    }
}

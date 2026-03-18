package com.wealthvault.`user-api`.updateuser

import com.wealthvault.`user-api`.model.UpdateUserDataRequest
import com.wealthvault.`user-api`.model.UpdateUserDataResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.patch
import io.ktor.client.request.setBody

class UpdateUserApiImpl(private val ktorfit: Ktorfit) : UpdateUserApi {
    override suspend fun updateUser(request: UpdateUserDataRequest): UpdateUserDataResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        // ยิงเองตรงๆ แบบไม่ง้อ Generator
        return client.patch("${Config.localhost_ios}user") {
            setBody(request)
        }.body()
    }
}

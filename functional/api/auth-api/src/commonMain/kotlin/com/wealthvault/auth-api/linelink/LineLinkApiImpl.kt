package com.wealthvault.`auth-api`.linelink

import com.wealthvault.`auth-api`.model.TokenRequest
import com.wealthvault.`auth-api`.model.TokenResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class LineLinkApiImpl(private val ktorfit: Ktorfit) : LineLinkApi {
    override suspend fun link(request: TokenRequest): TokenResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        // ยิงเองตรงๆ แบบไม่ง้อ Generator
        return client.post("${Config.localhost_android}auth/line/link") {
            setBody(request)
        }.body()
    }
}

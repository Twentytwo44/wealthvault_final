package com.example.`user-api`.user

import com.example.`user-api`.model.UserDataResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class UserApiImpl(private val ktorfit: Ktorfit) : UserApi {
    override suspend fun getuser(): UserDataResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}/user") {

        }.body()
    }
}

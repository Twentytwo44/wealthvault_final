package com.example.`user-api`.addfriend

import com.example.`user-api`.model.AcceptFriendRequest
import com.example.`user-api`.model.AcceptFriendResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AddFriendApiImpl(private val ktorfit: Ktorfit) : AddFriendApi {
    override suspend fun addFriend(request: AcceptFriendRequest): AcceptFriendResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        // ยิงเองตรงๆ แบบไม่ง้อ Generator
        return client.post("${Config.localhost_ios}friend/accept") {
            setBody(request)
        }.body()
    }
}

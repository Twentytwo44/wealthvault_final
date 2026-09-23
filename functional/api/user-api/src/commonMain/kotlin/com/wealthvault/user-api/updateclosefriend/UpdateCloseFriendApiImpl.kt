package com.wealthvault.`user-api`.updateclosefriend

import com.wealthvault.config.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post // 🌟 ใช้ POST ตาม Postman
import io.ktor.client.request.setBody

class UpdateCloseFriendApiImpl(
    private val client: HttpClient,
) : UpdateCloseFriendApi {

    override suspend fun updateCloseFriend(
        friendId: String,
        isClose: Boolean
    ): Boolean {

        // 🌟 ยิง POST ผ่าน singleton authenticated client
        return client.post("${Config.localhost_android}closefriend") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("friend_id", friendId)
                        append("is_close", isClose.toString()) // ส่งเป็น "true" หรือ "false"
                    }
                )
            )
        }.body<com.wealthvault.`user-api`.model.UpdateCloseFriendResponse>().data?.success
            ?: throw IllegalStateException("Update close friend response is empty")
    }
}

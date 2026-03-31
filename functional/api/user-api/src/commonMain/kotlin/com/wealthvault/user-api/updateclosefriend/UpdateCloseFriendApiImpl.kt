package com.wealthvault.`user-api`.updateclosefriend

import com.wealthvault.config.Config
import com.wealthvault.data_store.TokenStore
import com.wealthvault_final.setup_api.HttpClientBuilder
import com.wealthvault.`user-api`.model.UpdateCloseFriendResponse
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post // 🌟 ใช้ POST ตาม Postman
import io.ktor.client.request.setBody
import kotlinx.serialization.json.Json

class UpdateCloseFriendApiImpl(
    private val tokenStore: TokenStore
) : UpdateCloseFriendApi {

    override suspend fun updateCloseFriend(
        friendId: String,
        isClose: Boolean
    ): UpdateCloseFriendResponse {

        val clientWithAuth = HttpClientBuilder(Json, tokenStore).build(withAuth = true)

        try {
            // 🌟 ยิง POST ไปที่ /api/closefriend
            return clientWithAuth.post("${Config.localhost_android}closefriend") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("friend_id", friendId)
                            append("is_close", isClose.toString()) // ส่งเป็น "true" หรือ "false"
                        }
                    )
                )
            }.body()
        } finally {
            clientWithAuth.close() // คืนทรัพยากร
        }
    }
}
package com.wealthvault.`user-api`.addfriend

import com.wealthvault.`user-api`.model.AcceptFriendResponse
import com.wealthvault.config.Config
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AddFriendApiImpl(private val ktorfit: Ktorfit) : AddFriendApi {
    override suspend fun addFriend(requesterId: String): AcceptFriendResponse {
        val client = ktorfit.httpClient

        // 🌟 แก้ URL เป็น /friend และส่งแบบ Form-data
        return client.post("${Config.localhost_android}friend") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("requester_id", requesterId)
                    }
                )
            )
        }.body()
    }
}
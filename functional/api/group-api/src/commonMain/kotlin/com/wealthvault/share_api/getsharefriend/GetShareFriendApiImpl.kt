package com.wealthvault.share_api.getsharefriend

import com.wealthvault.config.Config
import com.wealthvault.share_api.model.ShareFriendResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetShareFriendApiImpl(private val ktorfit: Ktorfit) : GetShareFriendApi {
    override suspend fun getShareFriend(id:String): ShareFriendResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}friend/${id}/item/") {
        }.body()
    }
}

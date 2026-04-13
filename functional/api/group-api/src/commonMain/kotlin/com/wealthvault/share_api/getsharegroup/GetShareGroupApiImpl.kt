package com.wealthvault.share_api.getsharegroup

import com.wealthvault.config.Config
import com.wealthvault.share_api.model.ShareGroupResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetShareGroupApiImpl(private val ktorfit: Ktorfit) : GetShareGroupApi {
    override suspend fun getShareGroup(id:String): ShareGroupResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}group/${id}/item/") {
        }.body()
    }
}

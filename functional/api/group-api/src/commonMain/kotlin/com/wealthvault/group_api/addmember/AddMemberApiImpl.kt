package com.wealthvault.group_api.addmember


import com.wealthvault.config.Config
import com.wealthvault.group_api.model.AddMemberRequest
import com.wealthvault.group_api.model.MemberResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AddMemberApiImpl(private val ktorfit: Ktorfit) : AddMemberApi {
    override suspend fun addMember(id: String, request: AddMemberRequest): MemberResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}group/${id}/") {
            setBody(request)
        }.body()
    }
}

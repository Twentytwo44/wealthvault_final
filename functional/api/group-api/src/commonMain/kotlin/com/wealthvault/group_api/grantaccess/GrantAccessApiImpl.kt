package com.wealthvault.group_api.grantaccess


import com.wealthvault.config.Config
import com.wealthvault.group_api.model.GrantAccessRequest
import com.wealthvault.group_api.model.MemberResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post

class GrantAccessApiImpl(private val ktorfit: Ktorfit) : GrantAccessApi {
    override suspend fun grantAccess(id: String, request: GrantAccessRequest): MemberResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}group/${id}/") {


        }.body()
    }
}

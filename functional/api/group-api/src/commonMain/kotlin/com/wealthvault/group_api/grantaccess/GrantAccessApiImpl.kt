package com.wealthvault.group_api.grantaccess

import com.wealthvault.config.Config
import com.wealthvault.group_api.model.GrantAccessRequest
import com.wealthvault.group_api.model.grantaccess
import com.wealthvault.domain.social.GrantAccess
import com.wealthvault.group_api.requireSuccess
import com.wealthvault.group_api.toWire
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GrantAccessApiImpl(private val client: HttpClient) : GrantAccessApi {
    override suspend fun grantAccess(id: String, request: GrantAccess): Boolean { // ✅ เปลี่ยนตรงนี้ด้วย
        return client.post("${Config.localhost_android}group/${id}/grantaccess/") {
            contentType(ContentType.Application.Json)
            setBody(request.toWire())
        }.body<grantaccess>().requireSuccess()
    }
}

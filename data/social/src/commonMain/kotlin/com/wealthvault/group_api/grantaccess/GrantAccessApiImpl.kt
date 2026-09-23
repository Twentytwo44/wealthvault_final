package com.wealthvault.data.social.group.transport.grantaccess

import com.wealthvault.config.Config
import com.wealthvault.data.social.group.transport.model.GrantAccessRequest
import com.wealthvault.data.social.group.transport.model.grantaccess
import com.wealthvault.domain.social.GrantAccess
import com.wealthvault.data.social.group.transport.requireSuccess
import com.wealthvault.data.social.group.transport.toWire
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

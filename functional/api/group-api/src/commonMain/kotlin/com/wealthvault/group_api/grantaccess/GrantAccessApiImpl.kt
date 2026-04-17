package com.wealthvault.group_api.grantaccess

import com.wealthvault.config.Config
import com.wealthvault.group_api.model.GrantAccessRequest
import com.wealthvault.group_api.model.grantaccess
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GrantAccessApiImpl(private val ktorfit: Ktorfit) : GrantAccessApi {
    override suspend fun grantAccess(id: String, request: GrantAccessRequest): grantaccess { // ✅ เปลี่ยนตรงนี้ด้วย
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}group/${id}/grantaccess/") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
package com.wealthvault.data.social.share.transport.getsharegroup

import com.wealthvault.config.Config
import com.wealthvault.data.social.share.transport.requireDomainData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetShareGroupApiImpl(private val client: HttpClient) : GetShareGroupApi {
    override suspend fun getShareGroup(id:String) = client
        .get("${Config.localhost_android}group/$id/item/")
        .body<com.wealthvault.data.social.share.transport.model.ShareGroupResponse>()
        .requireDomainData()
}

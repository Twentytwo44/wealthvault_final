package com.wealthvault.share_api.getsharegroup

import com.wealthvault.config.Config
import com.wealthvault.share_api.requireDomainData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetShareGroupApiImpl(private val client: HttpClient) : GetShareGroupApi {
    override suspend fun getShareGroup(id:String) = client
        .get("${Config.localhost_android}group/$id/item/")
        .body<com.wealthvault.share_api.model.ShareGroupResponse>()
        .requireDomainData()
}

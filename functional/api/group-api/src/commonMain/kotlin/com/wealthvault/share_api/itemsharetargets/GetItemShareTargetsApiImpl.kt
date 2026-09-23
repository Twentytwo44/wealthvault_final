package com.wealthvault.share_api.itemsharetargets

import com.wealthvault.config.Config
import com.wealthvault.share_api.requireDomainData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetItemShareTargetsApiImpl(private val client: HttpClient) : GetItemShareTargetsApi {
    override suspend fun getItemShareTargets(type:String, id:String) = client
        .get("${Config.localhost_android}share/item/${type}/${id}/shared-targets")
        .body<com.wealthvault.share_api.model.ItemShareTargetsResponse>()
        .requireDomainData()
}

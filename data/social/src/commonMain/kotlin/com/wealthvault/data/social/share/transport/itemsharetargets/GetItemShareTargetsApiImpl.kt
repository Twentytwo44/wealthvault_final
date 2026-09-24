package com.wealthvault.data.social.share.transport.itemsharetargets

import com.wealthvault.config.Config
import com.wealthvault.data.social.share.transport.requireDomainData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetItemShareTargetsApiImpl(private val client: HttpClient) : GetItemShareTargetsApi {
    override suspend fun getItemShareTargets(type:String, id:String) = client
        .get("${Config.localhost_android}share/item/${type}/${id}/shared-targets")
        .body<com.wealthvault.data.social.share.transport.model.ItemShareTargetsResponse>()
        .requireDomainData()
}

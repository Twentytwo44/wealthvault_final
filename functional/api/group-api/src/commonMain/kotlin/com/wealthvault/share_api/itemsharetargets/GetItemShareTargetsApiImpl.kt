package com.wealthvault.share_api.itemsharetargets

import com.wealthvault.config.Config
import com.wealthvault.share_api.model.ItemShareTargetsResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.get

class GetItemShareTargetsApiImpl(private val ktorfit: Ktorfit) : GetItemShareTargetsApi {
    override suspend fun getItemShareTargets(type:String, id:String): ItemShareTargetsResponse {
        // ใช้ HttpClient ที่อยู่ใน Ktorfit ส่งค่าออกไปจริงๆ
        val client = ktorfit.httpClient

        return client.get("${Config.localhost_android}share/item/${type}/${id}/shared-targets") {
        }.body()
    }
}

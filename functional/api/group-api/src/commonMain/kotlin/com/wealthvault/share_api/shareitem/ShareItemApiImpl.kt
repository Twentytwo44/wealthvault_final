package com.wealthvault.share_api.shareitem


import com.wealthvault.config.Config
import com.wealthvault.share_api.model.ShareItemRequest
import com.wealthvault.share_api.model.ShareItemResponse
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Parameters

class ShareItemApiImpl(private val ktorfit: Ktorfit) : ShareItemApi {
    override suspend fun shareItem(request: ShareItemRequest): ShareItemResponse {
        val client = ktorfit.httpClient

        return client.post("${Config.localhost_android}share/item/") {
            // สร้าง Form Data พร้อมวนลูปสร้าง Key แบบมี Index
            val formData = Parameters.build {
                request.itemIds?.let { append("item_ids", it) }
                request.itemTypes?.let { append("item_types", it) }

                // วนลูป List ของ emails เพื่อสร้าง emails[id], emails[id], ...
                request.emails?.forEachIndexed { index, item ->
                    item.id?.let { append("emails[$index][id]", it) }
                    item.shareAt?.let { append("emails[$index][share_at]", it) }
                }

                // วนลูป List ของ friends
                request.friends?.forEachIndexed { index, item ->
                    item.id?.let { append("friends[$index][id]", it) }
                    item.shareAt?.let { append("friends[$index][share_at]", it) }
                }

                // วนลูป List ของ groups
                request.groups?.forEachIndexed { index, item ->
                    item.id?.let { append("groups[$index][id]", it) }
                    item.shareAt?.let { append("groups[$index][share_at]", it) }
                }
            }
        println("formData: $formData")
            // เซ็ต Body เป็น Form Data
            setBody(FormDataContent(formData))
        }.body()
    }
}

package com.wealthvault.data.social.share.transport.shareitem


import com.wealthvault.config.Config
import com.wealthvault.domain.social.ShareItems
import com.wealthvault.data.social.share.transport.model.ShareItemResponse
import com.wealthvault.data.social.share.transport.requireSuccess
import com.wealthvault.data.social.share.transport.toWire
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Parameters

class ShareItemApiImpl(private val client: HttpClient) : ShareItemApi {
    override suspend fun shareItem(request: ShareItems): Boolean {
        val wireRequest = request.toWire()
        return client.post("${Config.localhost_android}share/item/") {
            // สร้าง Form Data พร้อมวนลูปสร้าง Key แบบมี Index
            val formData = Parameters.build {
                wireRequest.itemIds?.let { append("item_ids", it) }
                wireRequest.itemTypes?.let { append("item_types", it) }

                // วนลูป List ของ emails เพื่อสร้าง emails[id], emails[id], ...
                wireRequest.emails?.forEachIndexed { index, item ->
                    item.id?.let { append("emails[$index][id]", it) }
                    item.shareAt?.let { append("emails[$index][share_at]", it) }
                }

                // วนลูป List ของ friends
                wireRequest.friends?.forEachIndexed { index, item ->
                    item.id?.let { append("friends[$index][id]", it) }
                    item.shareAt?.let { append("friends[$index][share_at]", it) }
                }

                // วนลูป List ของ groups
                wireRequest.groups?.forEachIndexed { index, item ->
                    item.id?.let { append("groups[$index][id]", it) }
                    item.shareAt?.let { append("groups[$index][share_at]", it) }
                }
            }
            // เซ็ต Body เป็น Form Data
            setBody(FormDataContent(formData))
        }.body<ShareItemResponse>().requireSuccess()
    }
}

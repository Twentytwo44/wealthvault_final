package com.wealthvault_final.`financial-asset`.data.share

import com.wealthvault.share_api.model.ShareItemRequest
import com.wealthvault.share_api.shareitem.ShareItemApi

class ShareItemNetworkDataSource(
    private val shareItemApi: ShareItemApi,
) {
    suspend fun shareItem(request: ShareItemRequest): Result<String> {
        return runCatching {
            val result = shareItemApi.shareItem(request)
            print("share item result:" + result)

            // ✅ แก้ไข: เช็กว่า data เป็น true หรือไม่ ถ้าใช่ถือว่าสำเร็จ
            if (result.data == true) {
                "Success"
            } else {
                // ถ้า data ไม่เป็น true ให้โยน error พร้อมข้อความจาก backend
                throw IllegalArgumentException(result.error ?: "Unknown Error")
            }
        }
    }
}
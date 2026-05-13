package com.wealthvault.notification_api.readall

import com.wealthvault.notification_api.model.NotificationResponse
import de.jensklingenberg.ktorfit.http.PUT // 🌟 เปลี่ยนจาก POST เป็น PUT

interface PutNotiReadAllApi {
    // 🌟 เปลี่ยนเป็น @PUT และเอา @Path("id") ออกเพราะ read-all ไม่ต้องระบุ id
    @PUT("notifications/read-all")
    suspend fun putNotiReadAll(): NotificationResponse
}
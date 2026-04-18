package com.wealthvault.group_api.leavegroup

import com.wealthvault.group_api.model.grantaccess
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface LeaveGroupApi {
    @DELETE("group/{id}/leave") // 🌟 เอา / ออกถ้า Backend ไม่ได้ใช้
    suspend fun leaveGroup(@Path("id") id: String): grantaccess // 🌟 เปลี่ยนมาใช้ตัวที่รับ Boolean
}
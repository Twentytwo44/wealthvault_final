package com.wealthvault.group_api.getgroupdetail

import com.wealthvault.group_api.model.GroupResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetGroupApi {
    @GET("group/detail/{id}")
    suspend fun getGroupDetail(@Path("id") id: String): GroupResponse // 🌟 เปลี่ยนชื่อฟังก์ชัน
}
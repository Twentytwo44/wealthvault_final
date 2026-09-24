package com.wealthvault.data.social.group.transport.getgroupdetail

import com.wealthvault.domain.social.GroupResult

interface GetGroupApi {
    suspend fun getGroupDetail(id: String): GroupResult // 🌟 เปลี่ยนชื่อฟังก์ชัน
}

package com.wealthvault.group_api.creategroup

import com.wealthvault.domain.social.GroupResult

interface CreateGroupApi {
    // 🌟 ไม่ต้องใช้ @POST ตรงนี้แล้ว เพราะเราจะเขียน Custom Request ใน Impl แทน
    suspend fun createGroup(
        groupName: String,
        memberIds: List<String>,
        imageBytes: ByteArray? // 🌟 รับรูปเป็น ByteArray เพื่อเตรียมอัปโหลด
    ): GroupResult
}

package com.wealthvault.group_api.creategroup

import com.wealthvault.group_api.model.GroupResponse

interface CreateGroupApi {
    // 🌟 ไม่ต้องใช้ @POST ตรงนี้แล้ว เพราะเราจะเขียน Custom Request ใน Impl แทน
    suspend fun createGroup(
        groupName: String,
        memberIds: List<String>,
        imageBytes: ByteArray? // 🌟 รับรูปเป็น ByteArray เพื่อเตรียมอัปโหลด
    ): GroupResponse
}
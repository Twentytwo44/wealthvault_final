package com.wealthvault.group_api.updategroup
import com.wealthvault.group_api.model.GroupResponse

interface UpdateGroupApi {
    suspend fun updateGroup(id: String, groupName: String, imageBytes: ByteArray?): GroupResponse
}
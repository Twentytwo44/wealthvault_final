package com.wealthvault.group_api.updategroup
import com.wealthvault.domain.social.GroupResult

interface UpdateGroupApi {
    suspend fun updateGroup(id: String, groupName: String, imageBytes: ByteArray?): GroupResult
}

package com.wealthvault.data.social.group.transport.updategroup
import com.wealthvault.domain.social.GroupResult

interface UpdateGroupApi {
    suspend fun updateGroup(id: String, groupName: String, imageBytes: ByteArray?): GroupResult
}

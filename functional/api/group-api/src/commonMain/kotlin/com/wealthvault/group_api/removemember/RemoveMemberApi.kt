package com.wealthvault.group_api.removemember
import com.wealthvault.group_api.model.MemberResponse

interface RemoveMemberApi {
    suspend fun removeMember(id: String, targetId: String): MemberResponse
}
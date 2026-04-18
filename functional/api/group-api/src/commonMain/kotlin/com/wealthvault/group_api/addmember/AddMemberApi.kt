package com.wealthvault.group_api.addmember
import com.wealthvault.group_api.model.MemberResponse

interface AddMemberApi {
    suspend fun addMember(id: String, targetId: String): MemberResponse
}
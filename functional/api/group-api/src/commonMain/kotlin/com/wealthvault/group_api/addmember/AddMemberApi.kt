package com.wealthvault.group_api.addmember
interface AddMemberApi {
    suspend fun addMember(id: String, targetId: String): Boolean
}

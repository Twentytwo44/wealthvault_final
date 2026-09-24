package com.wealthvault.data.social.group.transport.addmember
interface AddMemberApi {
    suspend fun addMember(id: String, targetId: String): Boolean
}

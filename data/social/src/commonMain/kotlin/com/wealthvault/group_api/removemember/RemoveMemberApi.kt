package com.wealthvault.data.social.group.transport.removemember
interface RemoveMemberApi {
    suspend fun removeMember(id: String, targetId: String): Boolean
}

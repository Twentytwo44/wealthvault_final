package com.wealthvault.group_api.removemember
interface RemoveMemberApi {
    suspend fun removeMember(id: String, targetId: String): Boolean
}

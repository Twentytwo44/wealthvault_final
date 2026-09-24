package com.wealthvault.data.social.group.transport.getmember

import com.wealthvault.domain.social.GroupMember

interface GetGroupMemberApi {
    suspend fun getGroupMembers(id: String): List<GroupMember>
}

package com.wealthvault.data.social.group.transport.groupmsg

import com.wealthvault.domain.social.GroupMessage

interface GetGroupMsgApi {
    suspend fun getGroupMsg(id: String): List<GroupMessage>
}

package com.wealthvault.data.social.group.transport.getgrouplist

import com.wealthvault.domain.social.GroupSummary

interface GetAllGroupApi {
    /** Transport-neutral group summaries; wire DTOs stay private to the adapter. */
    suspend fun getAllGroup(): List<GroupSummary>
}

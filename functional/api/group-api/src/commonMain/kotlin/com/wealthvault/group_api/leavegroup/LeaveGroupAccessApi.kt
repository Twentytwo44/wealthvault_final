package com.wealthvault.group_api.leavegroup

import com.wealthvault.group_api.model.MemberResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface LeaveGroupAccessApi {
    @DELETE("group/{id}/leave/")
    suspend fun leaveGroup(@Path("id") id: String): MemberResponse
}

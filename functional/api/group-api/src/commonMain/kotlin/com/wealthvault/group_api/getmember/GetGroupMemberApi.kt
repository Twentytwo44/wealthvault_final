package com.wealthvault.group_api.getmember

import com.wealthvault.group_api.model.GroupMemberResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetGroupMemberApi {
    @GET("group/member/{id}/")
    suspend fun getGroupMembers(@Path("id") id: String): GroupMemberResponse
}
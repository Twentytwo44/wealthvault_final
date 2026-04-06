package com.wealthvault.group_api.groupmsg

import com.wealthvault.group_api.model.GroupMsgResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetGroupMsgApi {
    @GET("group/{id}/msg/")
    suspend fun getGroupMsg(@Path("id") id: String): GroupMsgResponse
}

package com.wealthvault.group_api.removemember

import com.wealthvault.group_api.model.AddMemberRequest
import com.wealthvault.group_api.model.MemberResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface RemoveMemberApi {
    @POST("group/{id}/removemember/")
    suspend fun removeMember(@Path("id") id: String, @Body request: AddMemberRequest): MemberResponse
}

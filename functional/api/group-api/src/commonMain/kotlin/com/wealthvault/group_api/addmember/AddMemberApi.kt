package com.wealthvault.group_api.addmember

import com.wealthvault.group_api.model.AddMemberRequest
import com.wealthvault.group_api.model.MemberResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface AddMemberApi {
    @POST("group/{id}/")
    suspend fun addMember(@Path("id") id: String, @Body request: AddMemberRequest): MemberResponse
}

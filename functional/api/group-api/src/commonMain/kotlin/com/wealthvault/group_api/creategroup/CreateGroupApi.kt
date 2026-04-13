package com.wealthvault.investment_api.createcash

import com.wealthvault.group_api.model.GroupRequest
import com.wealthvault.group_api.model.GroupResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface CreateGroupApi {
    @POST("asset/group/")
    suspend fun createGroup(@Body request: GroupRequest): GroupResponse
}

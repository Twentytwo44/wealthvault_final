package com.wealthvault.group_api.getgrouplist

import com.wealthvault.group_api.model.GetGroupResponse
import de.jensklingenberg.ktorfit.http.GET

interface GetAllGroupApi {
    @GET("group/")
    suspend fun getAllGroup(): GetGroupResponse
}

package com.wealthvault.group_api.updategroup

import com.wealthvault.group_api.model.GroupRequest
import com.wealthvault.group_api.model.GroupResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.Path

interface UpdateGroupApi {
    @PATCH("group/{id}/")
    suspend fun updateGroup(@Path("id") id: String, @Body request: GroupRequest): GroupResponse
}

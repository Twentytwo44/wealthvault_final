package com.wealthvault.group_api.deletegroup

import com.wealthvault.group_api.model.DeleteGroupResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface DeleteGroupApi {
    @DELETE("group/{id}")
    suspend fun deleteGroup(@Path("id") id: String): DeleteGroupResponse
}
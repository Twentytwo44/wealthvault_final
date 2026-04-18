package com.wealthvault.group_api.grantaccess

import com.wealthvault.group_api.model.GrantAccessRequest
import com.wealthvault.group_api.model.grantaccess
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface GrantAccessApi {
    @POST("group/{id}/grantaccess/")
    suspend fun grantAccess(
        @Path("id") id: String,
        @Body request: GrantAccessRequest
    ): grantaccess // ✅ เปลี่ยนจาก GroupMemberResponse เป็น GrantAccessResponse
}
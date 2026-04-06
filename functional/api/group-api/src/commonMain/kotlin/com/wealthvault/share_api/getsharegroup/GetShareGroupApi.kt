package com.wealthvault.share_api.getsharegroup

import com.wealthvault.share_api.model.ShareGroupResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetShareGroupApi {
    @GET("group/{id}/item/")
    suspend fun getShareGroup(@Path("id") id: String): ShareGroupResponse

}

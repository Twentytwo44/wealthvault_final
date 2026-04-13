package com.wealthvault.share_api.unsharegroup

import com.wealthvault.share_api.model.ShareItemResponse
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Path

interface UnShareGroupApi {
    @DELETE("group/item/{id}/")
    suspend fun unShareGroup(@Path("id") id: String): ShareItemResponse
}

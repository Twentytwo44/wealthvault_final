package com.wealthvault.share_api.itemsharetargets

import com.wealthvault.share_api.model.ItemShareTargetsResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetItemShareTargetsApi {
    @GET("share/item/{type}/{id}/share-targets")
    suspend fun getItemShareTargets(@Path("type") type: String, @Path("id") id: String): ItemShareTargetsResponse

}

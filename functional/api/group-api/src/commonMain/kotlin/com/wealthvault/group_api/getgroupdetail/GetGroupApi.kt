package com.wealthvault.group_api.getgroupdetail

import com.wealthvault.group_api.model.GroupResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GetGroupApi {
    @GET("asset/invest/{id}")
    suspend fun getInvestment(@Path("id") id: String): GroupResponse
}

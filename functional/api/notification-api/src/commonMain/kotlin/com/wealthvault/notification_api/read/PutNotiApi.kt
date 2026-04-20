package com.wealthvault.notification_api.read

import com.wealthvault.notification_api.model.NotificationResponse
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface PutNotiApi {
    @POST("notifications/{id}/")
    suspend fun putNoti(@Path("id") id: String): NotificationResponse
}

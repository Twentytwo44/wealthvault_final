package com.wealthvault.data.notification.transport.read

interface PutNotiApi {
    suspend fun putNoti(id: String)
}

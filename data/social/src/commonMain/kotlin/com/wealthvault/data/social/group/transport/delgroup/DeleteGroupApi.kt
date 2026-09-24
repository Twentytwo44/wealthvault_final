package com.wealthvault.data.social.group.transport.deletegroup

interface DeleteGroupApi {
    suspend fun deleteGroup(id: String): Boolean
}

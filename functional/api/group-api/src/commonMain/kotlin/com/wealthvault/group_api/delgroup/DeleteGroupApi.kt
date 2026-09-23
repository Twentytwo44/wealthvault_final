package com.wealthvault.group_api.deletegroup

interface DeleteGroupApi {
    suspend fun deleteGroup(id: String): Boolean
}

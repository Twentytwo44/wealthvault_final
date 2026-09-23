package com.wealthvault.data.social.share.transport.unsharegroup

interface UnShareGroupApi {
    suspend fun unShareGroup(id: String)
}

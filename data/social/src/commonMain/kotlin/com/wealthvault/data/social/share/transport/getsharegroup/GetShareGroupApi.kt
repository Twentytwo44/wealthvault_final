package com.wealthvault.data.social.share.transport.getsharegroup

import com.wealthvault.domain.social.ShareGroup

interface GetShareGroupApi {
    suspend fun getShareGroup(id: String): List<ShareGroup>

}

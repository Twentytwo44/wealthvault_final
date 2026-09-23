package com.wealthvault.share_api.getsharegroup

import com.wealthvault.domain.social.ShareGroup

interface GetShareGroupApi {
    suspend fun getShareGroup(id: String): List<ShareGroup>

}

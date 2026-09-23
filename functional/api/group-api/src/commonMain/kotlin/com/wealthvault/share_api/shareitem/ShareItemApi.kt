package com.wealthvault.share_api.shareitem

import com.wealthvault.domain.social.ShareItems

interface ShareItemApi {
    suspend fun shareItem(request: ShareItems): Boolean
}

package com.wealthvault.data.social.share.transport.shareitem

import com.wealthvault.domain.social.ShareItems

interface ShareItemApi {
    suspend fun shareItem(request: ShareItems): Boolean
}

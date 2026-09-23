package com.wealthvault.social.data.share

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.data.social.share.transport.shareitem.ShareItemApi
import com.wealthvault.domain.social.ShareItems

internal class ShareItemNetworkDataSource(
    private val shareItemApi: ShareItemApi,
) {
    suspend fun shareItem(request: ShareItems): AppResult<String> = runSuspendAppCatching {
        if (shareItemApi.shareItem(request)) "Success"
        else throw IllegalArgumentException("Share request was rejected")
    }
}

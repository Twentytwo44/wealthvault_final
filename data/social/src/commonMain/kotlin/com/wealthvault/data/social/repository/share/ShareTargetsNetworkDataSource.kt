package com.wealthvault.data.social.repository.share

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.data.social.share.transport.itemsharetargets.GetItemShareTargetsApi
import com.wealthvault.domain.social.ShareTargets

internal class ShareTargetsNetworkDataSource(
    private val shareTargetsApi: GetItemShareTargetsApi,
) {
    suspend fun shareTargets(id: String, type: String): AppResult<ShareTargets> =
        runSuspendAppCatching { shareTargetsApi.getItemShareTargets(type, id) }
}

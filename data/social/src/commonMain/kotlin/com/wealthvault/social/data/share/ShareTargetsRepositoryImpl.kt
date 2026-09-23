package com.wealthvault.social.data.share

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.map
import com.wealthvault.domain.social.ShareTargets
import com.wealthvault.domain.social.ShareTargetsRepository

internal class ShareTargetsRepositoryImpl(
    private val networkDataSource: ShareTargetsNetworkDataSource,
) : ShareTargetsRepository {
    override suspend fun shareTargets(id: String, type: String): AppResult<ShareTargets> =
        networkDataSource.shareTargets(id, type).map { it }
}

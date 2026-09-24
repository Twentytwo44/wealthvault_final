package com.wealthvault.data.social.repository.share

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.map
import com.wealthvault.domain.social.ShareItemRepository
import com.wealthvault.domain.social.ShareItems

internal class ShareItemRepositoryImpl(
    private val networkDataSource: ShareItemNetworkDataSource,
) : ShareItemRepository {
    override suspend fun shareItem(request: ShareItems): AppResult<String> =
        networkDataSource.shareItem(request).map { it }
}

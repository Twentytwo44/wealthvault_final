package com.wealthvault.data.social.share.transport.itemsharetargets

import com.wealthvault.domain.social.ShareTargets

interface GetItemShareTargetsApi {
    suspend fun getItemShareTargets(type: String, id: String): ShareTargets

}

package com.wealthvault.share_api.itemsharetargets

import com.wealthvault.domain.social.ShareTargets

interface GetItemShareTargetsApi {
    suspend fun getItemShareTargets(type: String, id: String): ShareTargets

}

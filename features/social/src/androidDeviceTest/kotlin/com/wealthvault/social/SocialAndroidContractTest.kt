package com.wealthvault.social

import com.wealthvault.social.ui.SocialUiAction
import com.wealthvault.social.ui.SocialUiData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SocialAndroidContractTest {
    @Test
    fun pendingBadgeStateIsExplicitAndRefreshable() {
        assertFalse(SocialUiData().hasPendingRequest)
        assertEquals(SocialUiAction.RefreshPendingBadge, SocialUiAction.RefreshPendingBadge)
    }
}

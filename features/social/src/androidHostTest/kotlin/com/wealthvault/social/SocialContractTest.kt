package com.wealthvault.social

import kotlin.test.Test
import com.wealthvault.domain.social.FriendProfile
import com.wealthvault.domain.social.MessageItem
import kotlin.test.assertEquals

class SocialContractTest {
    @Test
    fun friendProfileAndMessageDefaultsAreDeterministic() {
        assertEquals(emptyList(), FriendProfile().itemPreview)
        assertEquals(null, MessageItem().metadata)
    }
}

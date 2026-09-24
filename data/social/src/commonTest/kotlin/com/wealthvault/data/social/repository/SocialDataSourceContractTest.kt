package com.wealthvault.data.social.repository

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.domain.social.GroupData
import com.wealthvault.domain.social.GroupResult
import kotlin.test.Test
import kotlin.test.assertEquals

class SocialDataSourceContractTest {
    @Test
    fun emptyGroupEnvelopeBecomesNotFound() {
        val result = AppResult.Success(GroupResult(status = "success", data = null))

        assertEquals(AppResult.Failure(AppError.NotFound), result.toGroupDataResult())
    }

    @Test
    fun populatedGroupEnvelopeStaysSuccessful() {
        val group = GroupData(id = "group-1", groupName = "Family", memberCount = 2)
        val result = AppResult.Success(GroupResult(status = "success", data = group))

        assertEquals(AppResult.Success(group), result.toGroupDataResult())
    }
}

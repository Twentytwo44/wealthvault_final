package com.wealthvault.data.portfolio.repository.group

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.domain.social.GroupDirectoryRepository
import com.wealthvault.domain.social.GroupSummary
import com.wealthvault.domain.social.SocialRepository

internal class GroupRepositoryImpl(
    private val socialRepository: SocialRepository,
) : GroupDirectoryRepository {
    override suspend fun getAllGroup(): AppResult<List<GroupSummary>> {
        return socialRepository.getAllGroups()
    }
}

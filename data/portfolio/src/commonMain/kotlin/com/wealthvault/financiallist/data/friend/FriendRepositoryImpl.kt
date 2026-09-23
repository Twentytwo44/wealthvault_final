package com.wealthvault.data.portfolio.repository.friend

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.domain.profile.FriendData
import com.wealthvault.domain.profile.FriendDirectoryRepository
import com.wealthvault.domain.profile.ProfileRepository

internal class FriendRepositoryImpl(
    private val profileRepository: ProfileRepository,
) : FriendDirectoryRepository {
    override suspend fun getFriend(): AppResult<List<FriendData>> {
        return profileRepository.getAllFriends()
    }
}

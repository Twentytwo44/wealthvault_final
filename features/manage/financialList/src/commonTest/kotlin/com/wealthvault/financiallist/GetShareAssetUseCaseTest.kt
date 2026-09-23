package com.wealthvault.financiallist

import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.domain.profile.FriendData
import com.wealthvault.domain.profile.FriendDirectoryRepository
import com.wealthvault.domain.social.GroupDirectoryRepository
import com.wealthvault.domain.social.GroupShareTarget
import com.wealthvault.domain.social.GroupSummary
import com.wealthvault.domain.social.FriendShareTarget
import com.wealthvault.domain.social.EmailShareTarget
import com.wealthvault.domain.social.ShareTargets
import com.wealthvault.domain.social.ShareTargetsRepository
import com.wealthvault.financiallist.ui.shareasset.usecase.GetShareAssetUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetShareAssetUseCaseTest {
    @Test
    fun mapsFriendsAndGroupsWithoutExposingTransportTypes() = runTest {
        val useCase = GetShareAssetUseCase(
            friendRepository = FakeFriends(
                AppResult.Success(
                    listOf(FriendData(id = "friend-1", username = "Natchapon", email = "n@example.com")),
                ),
            ),
            groupRepository = FakeGroups(
                AppResult.Success(listOf(GroupSummary(id = "group-1", groupName = "Family", memberCount = 3))),
            ),
            shareTargetsRepository = FakeShareTargets(
                AppResult.Success(
                    ShareTargets(
                        groups = listOf(GroupShareTarget(groupId = "group-1", sharedAt = "2026-09-13")),
                        friends = listOf(FriendShareTarget(friendId = "friend-1", sharedAt = "2026-09-12")),
                        emails = listOf(EmailShareTarget(email = "invite@example.com", sharedAt = "2026-09-14", isSent = true)),
                    ),
                ),
            ),
        )

        val result = assertIs<AppResult.Success<*>>(useCase("asset-1", "cash"))
        val data = result.value as com.wealthvault.financiallist.ui.shareasset.model.ShareCombinedData

        assertEquals(true, data.mappedGroups.single().isShared)
        assertEquals("2026-09-13", data.mappedGroups.single().sharedAt)
        assertEquals(true, data.mappedFriends.single().isShared)
        assertEquals("n@example.com", data.mappedFriends.single().email)
        assertEquals("invite@example.com", data.mappedEmails.single().userId)
        assertEquals("2026-09-14", data.mappedEmails.single().apiDate)
    }

    @Test
    fun propagatesReadFailureInsteadOfShowingAnEmptySelection() = runTest {
        val failure = AppError.Network(IllegalStateException("offline"))
        val useCase = GetShareAssetUseCase(
            friendRepository = FakeFriends(AppResult.Failure(failure)),
            groupRepository = FakeGroups(AppResult.Success(emptyList())),
            shareTargetsRepository = FakeShareTargets(AppResult.Success(ShareTargets())),
        )

        val result = assertIs<AppResult.Failure>(useCase("asset-1", "cash"))

        assertEquals(failure, result.error)
    }

    private class FakeFriends(
        private val result: AppResult<List<FriendData>>,
    ) : FriendDirectoryRepository {
        override suspend fun getFriend(): AppResult<List<FriendData>> = result
    }

    private class FakeGroups(
        private val result: AppResult<List<GroupSummary>>,
    ) : GroupDirectoryRepository {
        override suspend fun getAllGroup(): AppResult<List<GroupSummary>> = result
    }

    private class FakeShareTargets(
        private val result: AppResult<ShareTargets>,
    ) : ShareTargetsRepository {
        override suspend fun shareTargets(id: String, type: String): AppResult<ShareTargets> = result
    }
}

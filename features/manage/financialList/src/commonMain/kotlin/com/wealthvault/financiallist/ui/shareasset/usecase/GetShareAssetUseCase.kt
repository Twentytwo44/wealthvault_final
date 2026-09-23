package com.wealthvault.financiallist.ui.shareasset.usecase

import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.toAppError
import com.wealthvault.domain.profile.FriendDirectoryRepository
import com.wealthvault.domain.social.GroupDirectoryRepository
import com.wealthvault.domain.social.ShareTargetsRepository
import com.wealthvault.financiallist.ui.shareasset.model.FriendTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.GroupTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.ShareInfo
import com.wealthvault.financiallist.ui.shareasset.model.ShareCombinedData
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.CancellationException

class GetShareAssetUseCase(
    private val friendRepository: FriendDirectoryRepository,
    private val groupRepository: GroupDirectoryRepository,
    private val shareTargetsRepository: ShareTargetsRepository,
) {
    suspend operator fun invoke(id: String, type: String): AppResult<ShareCombinedData> = try {
        coroutineScope {
            val shareItemDeferred = async { shareTargetsRepository.shareTargets(id, type) }
            val groupDeferred = async { groupRepository.getAllGroup() }
            val friendDeferred = async { friendRepository.getFriend() }

            val shareTargetsResult = shareItemDeferred.await()
            val groupsResult = groupDeferred.await()
            val friendsResult = friendDeferred.await()

            // A share form must not look empty when one of its three reads
            // failed. Propagate the first typed error so the ScreenModel can
            // keep the existing selection state and surface a retryable error.
            val failure = listOf(shareTargetsResult, groupsResult, friendsResult)
                .firstOrNull { it is AppResult.Failure } as? AppResult.Failure
            if (failure != null) {
                return@coroutineScope AppResult.Failure(failure.error)
            }

            val shareTargets = (shareTargetsResult as AppResult.Success).value
            val allGroups = (groupsResult as AppResult.Success).value
            val allFriends = (friendsResult as AppResult.Success).value

            val sharedGroupsMap = shareTargets.groups?.associateBy { it.groupId }.orEmpty()
            val sharedFriendsMap = shareTargets.friends?.associateBy { it.friendId }.orEmpty()

            val mappedGroups = allGroups.map { group ->
                val sharedData = sharedGroupsMap[group.id]
                GroupTargetModel(
                    groupId = group.id ?: "",
                    groupName = group.groupName ?: "",
                    memberCount = group.memberCount ?: 0,
                    groupProfile = group.groupProfile,
                    isShared = sharedData != null,
                    sharedAt = sharedData?.sharedAt,
                )
            }

            val mappedFriends = allFriends.map { friend ->
                val sharedData = sharedFriendsMap[friend.id]
                FriendTargetModel(
                    friendId = friend.id ?: "",
                    friendName = friend.username ?: "",
                    email = friend.email ?: "",
                    profile = friend.profile,
                    isShared = sharedData != null,
                    sharedAt = sharedData?.sharedAt,
                )
            }

            val mappedEmails = shareTargets.emails.orEmpty().mapNotNull { emailTarget ->
                val email = emailTarget.email?.trim().orEmpty()
                if (email.isBlank()) {
                    null
                } else {
                    ShareInfo(
                        name = email,
                        userId = email,
                        typeData = "E",
                        subText = email,
                        isShared = true,
                        date = emailTarget.sharedAt,
                        apiDate = emailTarget.sharedAt,
                    )
                }
            }

            AppResult.Success(
                ShareCombinedData(
                    mappedGroups = mappedGroups,
                    mappedFriends = mappedFriends,
                    mappedEmails = mappedEmails,
                ),
            )
        }
    } catch (error: CancellationException) {
        throw error
    } catch (error: Throwable) {
        AppResult.Failure(error.toAppError())
    }
}

package com.wealthvault.financiallist.ui.shareasset.usecase

import com.wealthvault.financiallist.data.friend.FriendRepositoryImpl
import com.wealthvault.financiallist.data.group.GroupRepositoryImpl
import com.wealthvault.financiallist.data.share.ShareTargetsRepositoryImpl
import com.wealthvault.financiallist.ui.shareasset.model.FriendTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.GroupTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.ShareCombinedData
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetShareAssetUseCase(
    private val friendRepository: FriendRepositoryImpl,
    private val groupRepository: GroupRepositoryImpl,
    private val shareTargetsRepository: ShareTargetsRepositoryImpl,
) {
    suspend operator fun invoke(id: String, type: String): Result<ShareCombinedData> = coroutineScope {
        try {
            // 🚨 1. แก้ไข: ใช้ตัวแปร id และ type แทนการ Hardcode
            val shareItemDeferred = async { shareTargetsRepository.shareTargets(id, type) }
            val groupDeferred = async { groupRepository.getAllGroup() }
            val friendDeferred = async { friendRepository.getFriend() }

            val shareTargets = shareItemDeferred.await().getOrNull()
            val allGroups = groupDeferred.await().getOrNull() ?: emptyList()
            val allFriends = friendDeferred.await().getOrNull() ?: emptyList()

            val sharedGroupIds = shareTargets?.groups?.map { it.groupId }?.toSet() ?: emptySet()
            val sharedFriendIds = shareTargets?.friends?.map { it.friendId }?.toSet() ?: emptySet()

            val mappedGroups = allGroups.map { group ->
                GroupTargetModel(
                    groupId = group.id ?: "",
                    groupName = group.groupName ?: "",
                    // 🌟 2. ดึงรูปภาพและจำนวนคนมา Map ให้ UI ใช้ (เช็กชื่อตัวแปร group.memberCount ให้ตรงกับ API จริง)
                    memberCount = group.memberCount ?: 0,
                    groupProfile = group.groupProfile,
                    isShared = sharedGroupIds.contains(group.id)
                )
            }

            val mappedFriends = allFriends.map { friend ->
                FriendTargetModel(
                    friendId = friend.id ?: "",
                    friendName = friend.username ?: "",
                    // 🌟 3. ดึงอีเมลและรูปโปรไฟล์มา Map ให้ UI ใช้ (เช็กชื่อตัวแปร friend.email ให้ตรงกับ API จริง)
                    email = friend.email ?: "",
                    profile = friend.profile,
                    isShared = sharedFriendIds.contains(friend.id)
                )
            }

            // ส่งข้อมูลที่ Map เสร็จแล้วกลับไปให้ ScreenModel
            Result.success(
                ShareCombinedData(
                    mappedGroups = mappedGroups,
                    mappedFriends = mappedFriends
                )
            )

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
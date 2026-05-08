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

            // 🌟 เปลี่ยนจาก .toSet() เป็นเอามาจับคู่เป็น Map(id -> data) เพื่อให้เข้าถึง sharedAt ได้
            val sharedGroupsMap = shareTargets?.groups?.associateBy { it.groupId } ?: emptyMap()
            val sharedFriendsMap = shareTargets?.friends?.associateBy { it.friendId } ?: emptyMap()

            val mappedGroups = allGroups.map { group ->
                val sharedData = sharedGroupsMap[group.id] // ดูว่ากลุ่มนี้เคยแชร์หรือยัง
                GroupTargetModel(
                    groupId = group.id ?: "",
                    groupName = group.groupName ?: "",
                    memberCount = group.memberCount ?: 0,
                    groupProfile = group.groupProfile,
                    isShared = sharedData != null, // ถ้ามีค่าแปลว่าแชร์แล้ว
                    sharedAt = sharedData?.sharedAt // 🌟 ดึงค่า sharedAt จาก API มาใส่
                )
            }

            val mappedFriends = allFriends.map { friend ->
                val sharedData = sharedFriendsMap[friend.id] // ดูว่าเพื่อนคนนี้เคยแชร์หรือยัง
                FriendTargetModel(
                    friendId = friend.id ?: "",
                    friendName = friend.username ?: "",
                    email = friend.email ?: "",
                    profile = friend.profile,
                    isShared = sharedData != null,
                    sharedAt = sharedData?.sharedAt // 🌟 ดึงค่า sharedAt จาก API มาใส่
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
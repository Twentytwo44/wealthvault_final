package com.wealthvault.financiallist.ui.shareasset.usecase

import com.wealthvault.financiallist.data.friend.FriendRepositoryImpl
import com.wealthvault.financiallist.data.group.GroupRepositoryImpl
import com.wealthvault.financiallist.data.share.ShareTargetsRepositoryImpl
import com.wealthvault.financiallist.ui.shareasset.model.FriendTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.GroupTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.ShareCombinedData
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

// 💡 1. สร้าง Data Class ไว้มัดรวมของ 3 อย่าง


// 💡 2. สร้าง UseCase ย้าย Repository มาฉีด (Inject) ไว้ที่นี่แทน
class GetShareAssetUseCase(
    private val friendRepository: FriendRepositoryImpl,
    private val groupRepository: GroupRepositoryImpl,
    private val shareTargetsRepository: ShareTargetsRepositoryImpl,
) {
    // ใช้ coroutineScope เพื่อดึง API พร้อมกัน
    suspend operator fun invoke(id: String, type: String): Result<ShareCombinedData> = coroutineScope {
        try {
            // 1. โหลดข้อมูล 3 เส้นพร้อมกัน (เหมือนเดิม)
            val shareItemDeferred = async { shareTargetsRepository.shareTargets("27c79801-6708-469b-8e96-a28394c27397", "account") } // JSON 1
            val groupDeferred = async { groupRepository.getAllGroup() }                      // JSON 2
            val friendDeferred = async { friendRepository.getFriend() }                      // JSON 3

            val shareTargets = shareItemDeferred.await().getOrNull()
            val allGroups = groupDeferred.await().getOrNull() ?: emptyList()
            val allFriends = friendDeferred.await().getOrNull() ?: emptyList()

            // 💡 2. ดึงเฉพาะ ID ของคนที่ "เคยแชร์แล้ว" ออกมาเก็บไว้ใน Set (เพื่อความรวดเร็วในการค้นหา)
            // ถ้า API ไม่มีข้อมูลมาให้ ให้ใช้ค่าว่าง (emptySet)
            val sharedGroupIds = shareTargets?.groups?.map { it.groupId }?.toSet() ?: emptySet()
            val sharedFriendIds = shareTargets?.friends?.map { it.friendId }?.toSet() ?: emptySet()

            // test log
            println("ALL TARGET: ${shareTargets}")
            println("=============================================")
            println("ALL GROUPS: ${allGroups}")
            println("=============================================")
            println("ALL FRIENDS: ${allFriends}")
            println("=============================================")

            println("SHARED GROUPS: ${sharedGroupIds}")
            println("=============================================")
            println("SHARED FRIENDS: ${sharedFriendIds}")
            println("=============================================")



            // 💡 3. Map ข้อมูล "กลุ่มทั้งหมด" เทียบกับ ID ที่เคยแชร์
            val mappedGroups = allGroups.map { group ->
                GroupTargetModel(
                    groupId = group.id ?: "",
                    groupName = group.groupName ?: "",
                    // เช็กว่า ID กลุ่มนี้ อยู่ในรายชื่อที่เคยแชร์ไหม (ถ้ามี = true, ไม่มี = false)
                    isShared = sharedGroupIds.contains(group.id)
                )
            }

            // 💡 4. Map ข้อมูล "เพื่อนทั้งหมด" เทียบกับ ID ที่เคยแชร์
            val mappedFriends = allFriends.map { friend ->
                FriendTargetModel(
                    friendId = friend.id ?: "",
                    friendName = friend.username
                        ?: "", // หรือจะใช้ "${friend.first_name} ${friend.last_name}" ก็ได้
                    // เช็กว่า ID เพื่อนคนนี้ อยู่ในรายชื่อที่เคยแชร์ไหม
                    isShared = sharedFriendIds.contains(friend.id)
                )
            }
            println("=============================================")

            println("MAPP GROUP ${mappedGroups}")
            println("=============================================")

            println("MAPP GROUP ${mappedFriends}")
            println("=============================================")



            // 5. ส่งข้อมูลที่ Map เสร็จแล้วกลับไปให้ ScreenModel
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

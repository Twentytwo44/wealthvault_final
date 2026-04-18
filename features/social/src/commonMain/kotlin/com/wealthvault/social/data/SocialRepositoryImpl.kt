package com.wealthvault.social.data

import com.wealthvault.account_api.model.BankAccountData
import com.wealthvault.building_api.model.BuildingIdData
import com.wealthvault.cash_api.model.CashIdData
import com.wealthvault.group_api.model.GetAllGroupData
import com.wealthvault.group_api.model.GroupData
import com.wealthvault.group_api.model.GroupMemberItem
import com.wealthvault.group_api.model.GroupMsgData
import com.wealthvault.`user-api`.model.FriendData
import com.wealthvault.group_api.model.GroupResponse
import com.wealthvault.insurance_api.model.InsuranceIdData
import com.wealthvault.investment_api.model.InvestmentIdData
import com.wealthvault.land_api.model.LandIdData
import com.wealthvault.liability_api.model.LiabilityIdData
import com.wealthvault.share_api.model.ItemToShareData
import com.wealthvault.share_api.model.ShareGroupData
import com.wealthvault.share_api.model.ShareItemRequest
import com.wealthvault.`user-api`.model.FriendProfileData
import com.wealthvault.`user-api`.model.MessageItem

class SocialRepositoryImpl(
    private val dataSource: SocialDataSource
) {
    suspend fun getAllFriends(): Result<List<FriendData>> {
        return dataSource.getAllFriends().onSuccess {
            println("✅ Fetched All Friends: ${it.size} persons")
        }.onFailure { error ->
            println("🚨 Get All Friends Failed: ${error.message}")
        }
    }

    suspend fun getAllGroups(): Result<List<GetAllGroupData>> {
        return dataSource.getAllGroups().onSuccess {
            println("✅ Fetched All Groups: ${it.size} groups")
        }.onFailure { error ->
            println("🚨 Get All Groups Failed: ${error.message}")
        }
    }

    suspend fun createGroup(
        groupName: String,
        memberIds: List<String>,
        imageBytes: ByteArray?
    ): Result<GroupResponse> {
        return dataSource.createGroup(groupName, memberIds, imageBytes).onSuccess {
            println("✅ Create Group Success: ${it.status}")
        }.onFailure { error ->
            println("🚨 Create Group Failed: ${error.message}")
        }
    }

    // 🌟 1. เพิ่มฟังก์ชันค้นหาเพื่อน
    suspend fun searchUser(email: String): Result<FriendData?> {
        return dataSource.searchUser(email).onSuccess {
            println("✅ Search User Success")
        }.onFailure { error ->
            println("🚨 Search User Failed: ${error.message}")
        }
    }

    // 🌟 2. เพิ่มฟังก์ชันเพิ่มเพื่อน
    suspend fun addFriend(targetId: String): Result<Boolean> {
        return dataSource.addFriend(targetId).onSuccess {
            println("✅ Add Friend Success")
        }.onFailure { error ->
            println("🚨 Add Friend Failed: ${error.message}")
        }
    }
    suspend fun getFriendMessages(friendId: String): Result<List<MessageItem>> {
        return dataSource.getFriendMessages(friendId).onSuccess {
            println("✅ Get Messages Success: ${it.size} items")
        }.onFailure { error ->
            println("🚨 Get Messages Failed: ${error.message}")
        }
    }
    suspend fun getFriendProfile(friendId: String): Result<FriendProfileData> {
        return dataSource.getFriendProfile(friendId).onSuccess {
            println("✅ Get Friend Profile Success: ${it.userInfo?.username}")
        }.onFailure { error ->
            println("🚨 Get Friend Profile Failed: ${error.message}")
        }
    }
    suspend fun getAccountById(id: String): Result<BankAccountData> = runCatching {
        val response = dataSource.getAccountById(id)
        response.data ?: throw Exception("ไม่พบข้อมูลบัญชีเงินฝาก")
    }
    suspend fun getBuildingById(id: String): Result<BuildingIdData> = runCatching {
        val response = dataSource.getBuildingById(id)
        response.data ?: throw Exception("ไม่พบข้อมูลอาคาร/สิ่งปลูกสร้าง")
    }
    suspend fun getCashById(id: String): Result<CashIdData> = runCatching {
        val response = dataSource.getCashById(id)
        response.data ?: throw Exception("ไม่พบข้อมูลเงินสด/ทองคำ")
    }
    suspend fun getInsuranceById(id: String): Result<InsuranceIdData> = runCatching {
        dataSource.getInsuranceById(id).data ?: throw Exception("ไม่พบข้อมูลประกัน")
    }
    suspend fun getInvestmentById(id: String): Result<InvestmentIdData> = runCatching {
        dataSource.getInvestmentById(id).data ?: throw Exception("ไม่พบข้อมูลการลงทุน")
    }
    suspend fun getLandById(id: String): Result<LandIdData> = runCatching {
        dataSource.getLandById(id).data ?: throw Exception("ไม่พบข้อมูลที่ดิน")
    }
    suspend fun getLiabilityById(id: String): Result<LiabilityIdData> = runCatching {
        dataSource.getLiabilityById(id).data ?: throw Exception("ไม่พบข้อมูลหนี้สิน/รายจ่าย")
    }


    suspend fun getGroupMessages(groupId: String): Result<List<GroupMsgData>> {
        return dataSource.getGroupMessages(groupId).onSuccess {
            println("✅ Get Group Messages Success: ${it.size} items")
        }.onFailure { error ->
            println("🚨 Get Group Messages Failed: ${error.message}")
        }
    }
    suspend fun getShareGroupItems(groupId: String): Result<List<ShareGroupData>> {
        return dataSource.getShareGroupItems(groupId).onSuccess {
            println("✅ Get Share Group Items Success: ${it.size} items")
        }.onFailure { error ->
            println("🚨 Get Share Group Items Failed: ${error.message}")
        }
    }

    suspend fun grantAccess(groupId: String, targetId: String, itemIds: List<String>): Result<Boolean> {
        return dataSource.grantAccess(groupId, targetId, itemIds).onSuccess {
            println("✅ Grant Access Success")
        }.onFailure { error ->
            println("🚨 Grant Access Failed: ${error.message}")
        }
    }

    suspend fun getGroupDetail(groupId: String): Result<GroupData> {
        return dataSource.getGroupDetail(groupId)
            .onSuccess { data ->
                println("✅ ดึงข้อมูลกลุ่มสำเร็จ: ${data.groupName} (สมาชิก ${data.memberCount} คน)")
            }
            .onFailure { error ->
                println("🚨 ดึงข้อมูลกลุ่มล้มเหลว: ${error.message}")
            }
    }
    suspend fun getGroupMembers(groupId: String): Result<List<GroupMemberItem>> {
        return dataSource.getGroupMembers(groupId)
    }
    // 🌟 1. อัปเดตข้อมูลกลุ่ม (ชื่อ, รูป)
    suspend fun updateGroup(groupId: String, groupName: String, profileImage: ByteArray? = null): Result<GroupResponse> {
        return dataSource.updateGroup(groupId, groupName, profileImage)
            .onSuccess {
                println("✅ อัปเดตกลุ่มสำเร็จ: $groupName")
            }
            .onFailure { error ->
                println("🚨 อัปเดตกลุ่มล้มเหลว: ${error.message}")
            }
    }

    // 🌟 2. เพิ่มสมาชิกเข้ากลุ่ม
    suspend fun addGroupMember(groupId: String, targetId: String): Result<Boolean> {
        return dataSource.addGroupMember(groupId, targetId)
            .onSuccess {
                println("✅ เพิ่มสมาชิกสำเร็จ (Target ID: $targetId)")
            }
            .onFailure { error ->
                println("🚨 เพิ่มสมาชิกล้มเหลว: ${error.message}")
            }
    }

    // 🌟 3. ลบสมาชิกออกจากกลุ่ม
    suspend fun removeGroupMember(groupId: String, targetId: String): Result<Boolean> {
        return dataSource.removeGroupMember(groupId, targetId)
            .onSuccess {
                println("✅ ลบสมาชิกสำเร็จ (Target ID: $targetId)")
            }
            .onFailure { error ->
                println("🚨 ลบสมาชิกล้มเหลว: ${error.message}")
            }
    }
    suspend fun leaveGroup(groupId: String): Result<Boolean> {
        return dataSource.leaveGroup(groupId)
            .onSuccess { println("✅ ออกจากกลุ่มสำเร็จ") }
            .onFailure { println("🚨 ออกจากกลุ่มล้มเหลว: ${it.message}") }
    }

    suspend fun removeFriend(targetId: String): Result<Boolean> {
        return dataSource.removeFriend(targetId)
            .onSuccess { println("✅ ลบเพื่อนสำเร็จ (Target ID: $targetId)") }
            .onFailure { println("🚨 ลบเพื่อนล้มเหลว: ${it.message}") }
    }
    suspend fun getItemsToShare(targetId: String, isGroup: Boolean): Result<List<ItemToShareData>> {
        return dataSource.getItemsToShare(targetId, isGroup)
            .onSuccess { println("✅ ดึงรายการที่สามารถแชร์ได้สำเร็จ: ${it.size} รายการ") }
            .onFailure { println("🚨 ดึงรายการแชร์ล้มเหลว: ${it.message}") }
    }
    suspend fun submitShareItems(request: ShareItemRequest): Result<Boolean> {
        return dataSource.submitShareItems(request)
            .onSuccess { println("✅ บันทึกการแชร์ทรัพย์สินสำเร็จ!") }
            .onFailure { println("🚨 บันทึกการแชร์ทรัพย์สินล้มเหลว: ${it.message}") }
    }
    suspend fun unShareAsset(sharedItemId: String, isGroup: Boolean): Result<Boolean> {
        return if (isGroup) {
            dataSource.unShareGroupItem(sharedItemId)
        } else {
            dataSource.unShareFriendItem(sharedItemId)
        }
    }


}
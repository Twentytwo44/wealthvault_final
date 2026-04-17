package com.wealthvault.social.data

import com.wealthvault.account_api.getaccountbyid.GetAccountByIdApi
import com.wealthvault.building_api.getbuildingbyid.GetBuildingByIdApi
import com.wealthvault.cash_api.getcashtbyid.GetCashByIdApi
import com.wealthvault.group_api.addmember.AddMemberApi
import com.wealthvault.`user-api`.friend.FriendApi
import com.wealthvault.`user-api`.model.FriendData
import com.wealthvault.group_api.getgrouplist.GetAllGroupApi
import com.wealthvault.group_api.model.GetAllGroupData
import com.wealthvault.group_api.creategroup.CreateGroupApi
import com.wealthvault.group_api.getgroupdetail.GetGroupApi
import com.wealthvault.group_api.getmember.GetGroupMemberApi
import com.wealthvault.group_api.grantaccess.GrantAccessApi
import com.wealthvault.group_api.groupmsg.GetGroupMsgApi
import com.wealthvault.group_api.leavegroup.LeaveGroupApi
import com.wealthvault.group_api.model.GrantAccessRequest
import com.wealthvault.group_api.model.GroupData
import com.wealthvault.group_api.model.GroupMemberItem
import com.wealthvault.group_api.model.GroupMsgData
import com.wealthvault.group_api.model.GroupResponse
import com.wealthvault.group_api.removemember.RemoveMemberApi
import com.wealthvault.group_api.updategroup.UpdateGroupApi
import com.wealthvault.insurance_api.getinsurancetbyid.GetInsuranceByIdApi
import com.wealthvault.investment_api.getinvestmentbyid.GetInvestmentByIdApi
import com.wealthvault.land_api.getlandbyid.GetLandByIdApi
import com.wealthvault.liability_api.getliabilitybyid.GetLiabilityByIdApi
import com.wealthvault.share_api.getitemtoshare.GetItemToShareApi
import com.wealthvault.share_api.getsharegroup.GetShareGroupApi
import com.wealthvault.share_api.model.ItemToShareData
import com.wealthvault.share_api.model.ShareGroupData
import com.wealthvault.share_api.model.ShareItemRequest
import com.wealthvault.share_api.shareitem.ShareItemApi
// 🌟 1. Import API 2 ตัวที่เพิ่งสร้างมา
import com.wealthvault.`user-api`.getuserbyemail.GetUserByEmailApi
import com.wealthvault.`user-api`.addfriend.AddFriendApi
import com.wealthvault.`user-api`.deletefriend.DeleteFriendApi
import com.wealthvault.`user-api`.friendmsg.GetFriendMsgApi
import com.wealthvault.`user-api`.friendprofile.GetFriendProfileApi
import com.wealthvault.`user-api`.model.FriendProfileData
import com.wealthvault.`user-api`.model.MessageItem

class SocialDataSource(
    private val friendApi: FriendApi,
    private val groupApi: GetAllGroupApi,
    private val createGroupApi: CreateGroupApi,
    private val getUserByEmailApi: GetUserByEmailApi,
    private val addFriendApi: AddFriendApi,
    private val getFriendMsgApi: GetFriendMsgApi,
    private val getFriendProfileApi: GetFriendProfileApi,

    private val getAccountByIdApi: GetAccountByIdApi,
    private val getCashByIdApi: GetCashByIdApi,
    private val getBuildingByIdApi: GetBuildingByIdApi,
    private val getInsuranceByIdApi: GetInsuranceByIdApi,
    private val getInvestmentByIdApi: GetInvestmentByIdApi,
    private val getLandByIdApi: GetLandByIdApi,
    private val getLiabilityByIdApi: GetLiabilityByIdApi,
    private val getGroupMsgApi: GetGroupMsgApi,
    private val getShareGroupApi: GetShareGroupApi,
    private val grantAccessApi: GrantAccessApi,
    private val getGroupApi: GetGroupApi,
    private val getGroupMemberApi: GetGroupMemberApi,
    private val updateGroupApi: UpdateGroupApi,
    private val addMemberApi: AddMemberApi,
    private val removeMemberApi: RemoveMemberApi,
    private val leaveGroupApi: LeaveGroupApi,
    private val deleteFriendApi: DeleteFriendApi,
    private val getItemToShareApi: GetItemToShareApi,
    private val shareItemApi: ShareItemApi,


    ) {
    suspend fun getAllFriends(): Result<List<FriendData>> {
        return runCatching {
            val result = friendApi.getFriend()
            result.data?.friend ?: emptyList()
        }
    }

    suspend fun getAllGroups(): Result<List<GetAllGroupData>> {
        return runCatching {
            val result = groupApi.getAllGroup()
            result.data ?: emptyList()
        }
    }

    suspend fun createGroup(
        groupName: String,
        memberIds: List<String>,
        imageBytes: ByteArray?
    ): Result<GroupResponse> {
        return runCatching {
            val result = createGroupApi.createGroup(groupName, memberIds, imageBytes)
            if (result.error != null) {
                throw Exception(result.error)
            }
            result
        }
    }

    // 🌟 2. เรียกใช้ API ค้นหาเพื่อน
    suspend fun searchUser(email: String): Result<FriendData?> {
        return runCatching {
            val response = getUserByEmailApi.searchUserByEmail(email)
            if (response.error != null) throw Exception(response.error)
            // ดึงคนแรกออกมา ถ้าไม่มีให้เป็น null
            response.data?.firstOrNull()
        }
    }

    // 🌟 3. เรียกใช้ API เพิ่มเพื่อน
    suspend fun addFriend(targetId: String): Result<Boolean> {
        return runCatching {
            // ไม่ต้องสร้าง AcceptFriendRequest แล้ว โยน String เข้าไปเลย!
            val response = addFriendApi.addFriend(targetId)

            if (response.error != null) throw Exception(response.error)
            true
        }
    }
    suspend fun getFriendMessages(friendId: String): Result<List<MessageItem>> {
        return runCatching {
            val response = getFriendMsgApi.getFriendMsg(friendId)
            response.messages ?: emptyList() // ถ้าไม่มีให้ส่ง List ว่างกลับไป
        }
    }
    suspend fun getFriendProfile(friendId: String): Result<FriendProfileData> {
        return runCatching {
            val response = getFriendProfileApi.getFriendProfile(friendId)
            // ถ้า data เป็น null ให้โยน Exception ออกไป เพื่อให้ตกเข้า block onFailure
            response.data ?: throw Exception("ไม่พบข้อมูลโปรไฟล์เพื่อน")
        }
    }

    suspend fun getAccountById(id: String) = getAccountByIdApi.getAccountById(id)
    suspend fun getBuildingById(id: String) = getBuildingByIdApi.getBuildingById(id)
    suspend fun getCashById(id: String) = getCashByIdApi.getCashById(id)
    suspend fun getInsuranceById(id: String) = getInsuranceByIdApi.getInsuranceById(id)
    suspend fun getInvestmentById(id: String) = getInvestmentByIdApi.getInvestmentById(id)
    suspend fun getLandById(id: String) = getLandByIdApi.getLandById(id)
    suspend fun getLiabilityById(id: String) = getLiabilityByIdApi.getLiabilityById(id)

    suspend fun getGroupMessages(groupId: String): Result<List<GroupMsgData>> {
        return runCatching {
            // อย่าลืม inject GetGroupMsgApi เข้ามาใน Constructor ของ SocialDataSource ด้วยนะครับ
            val response = getGroupMsgApi.getGroupMsg(groupId)
            if (response.error != null) throw Exception(response.error)
            response.data
        }
    }
    suspend fun getShareGroupItems(groupId: String): Result<List<ShareGroupData>> {
        return runCatching {
            // เรียกใช้ ApiImpl ที่คุณ Champ สร้างไว้
            val response = getShareGroupApi.getShareGroup(groupId)

            // เช็คว่า Error จาก API (ที่เป็น String) มีค่าส่งมาไหม
            if (response.error != null) {
                throw Exception(response.error)
            }

            // ส่งข้อมูล List<ShareGroupData> กลับไป (ถ้า null ให้เป็น emptyList)
            response.data ?: emptyList()
        }
    }
    suspend fun grantAccess(groupId: String, targetId: String, itemIds: List<String>): Result<Boolean> {
        return runCatching {
            val request = GrantAccessRequest(targetId = targetId, itemIds = itemIds)
            // grantAccessApi คือตัวแปรที่คุณ Champ ต้องรับผ่าน Constructor มานะครับ
            val response = grantAccessApi.grantAccess(groupId, request)
            if (response.error != null) throw Exception(response.error)
            true
        }
    }

    suspend fun getGroupDetail(groupId: String): Result<GroupData> {
        return runCatching {
            val response = getGroupApi.getGroupDetail(groupId)

            // เช็คว่ามี Error ส่งกลับมาจาก Backend ไหม
            if (response.error != null) {
                throw Exception(response.error)
            }

            // เช็คว่ามีข้อมูล Data ไหม ถ้าไม่มีให้โยน Exception
            response.data ?: throw Exception("ไม่พบข้อมูลกลุ่ม หรือข้อมูลว่างเปล่า")
        }
    }
    suspend fun getGroupMembers(groupId: String): Result<List<GroupMemberItem>> {
        return runCatching {
            val response = getGroupMemberApi.getGroupMembers(groupId)
            if (response.error != null) throw Exception(response.error)

            // 🌟 ดึงเอาเฉพาะ List ของ members ออกมาส่งต่อ
            response.data?.members ?: emptyList()
        }
    }
    suspend fun updateGroup(groupId: String, groupName: String, profileImage: ByteArray? = null): Result<GroupResponse> {
        return runCatching {
            // 🌟 โยนค่าตรงๆ เข้าไปได้เลย
            val response = updateGroupApi.updateGroup(groupId, groupName, profileImage)
            if (response.error != null) throw Exception(response.error)
            response
        }
    }

    suspend fun addGroupMember(groupId: String, targetId: String): Result<Boolean> {
        return runCatching {
            val response = addMemberApi.addMember(groupId, targetId)
            if (response.error != null) throw Exception(response.error)
            true
        }
    }

    suspend fun removeGroupMember(groupId: String, targetId: String): Result<Boolean> {
        return runCatching {
            val response = removeMemberApi.removeMember(groupId, targetId)
            if (response.error != null) throw Exception(response.error)
            true
        }
    }
    suspend fun leaveGroup(groupId: String): Result<Boolean> {
        return runCatching {
            val response = leaveGroupApi.leaveGroup(groupId)
            if (response.error != null) throw Exception(response.error)
            true
        }
    }
    suspend fun removeFriend(targetId: String): Result<Boolean> {
        return runCatching {
            val response = deleteFriendApi.deleteFriend(targetId)
            if (response.error != null) throw Exception(response.error)
            true
        }
    }
    // ใน SocialDataSource.kt

    suspend fun getItemsToShare(targetId: String, isGroup: Boolean): Result<List<ItemToShareData>> {
        return runCatching {
            // 🌟 แปลง Boolean เป็น String ที่ Backend ต้องการ
            val type = if (isGroup) "group" else "friend"

            val response = getItemToShareApi.getItemsToShare(type, targetId)

            if (response.error != null) throw Exception(response.error)
            response.items ?: emptyList()
        }
    }
    suspend fun submitShareItems(request: ShareItemRequest): Result<Boolean> {
        return runCatching {
            val response = shareItemApi.shareItem(request)

            // เช็คว่ามี error ส่งกลับมาไหม
            if (response.error != null) {
                throw Exception(response.error)
            }

            // ถ้าสำเร็จให้คืนค่า true
            response.data ?: true
        }
    }

}
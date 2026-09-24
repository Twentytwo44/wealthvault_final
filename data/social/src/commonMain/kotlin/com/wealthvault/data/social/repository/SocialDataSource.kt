package com.wealthvault.data.social.repository

import com.wealthvault.data.social.group.transport.addmember.AddMemberApi
import com.wealthvault.data.social.group.transport.getgrouplist.GetAllGroupApi
import com.wealthvault.data.social.group.transport.creategroup.CreateGroupApi
import com.wealthvault.data.social.group.transport.deletegroup.DeleteGroupApi
import com.wealthvault.data.social.group.transport.getgroupdetail.GetGroupApi
import com.wealthvault.data.social.group.transport.getmember.GetGroupMemberApi
import com.wealthvault.data.social.group.transport.grantaccess.GrantAccessApi
import com.wealthvault.data.social.group.transport.groupmsg.GetGroupMsgApi
import com.wealthvault.data.social.group.transport.leavegroup.LeaveGroupApi
import com.wealthvault.data.social.group.transport.removemember.RemoveMemberApi
import com.wealthvault.data.social.group.transport.updategroup.UpdateGroupApi
import com.wealthvault.data.social.share.transport.getitemtoshare.GetItemToShareApi
import com.wealthvault.data.social.share.transport.getsharefriend.GetShareFriendApi
import com.wealthvault.data.social.share.transport.getsharegroup.GetShareGroupApi
import com.wealthvault.data.social.share.transport.shareitem.ShareItemApi
import com.wealthvault.data.social.share.transport.unsharefriend.UnShareFriendApi
import com.wealthvault.data.social.share.transport.unsharegroup.UnShareGroupApi
import com.wealthvault.core.observability.AppLogger
import com.wealthvault.core.observability.platformLogger
import com.wealthvault.core.architecture.AppResult
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.map
import com.wealthvault.core.architecture.runSuspendAppCatching
import com.wealthvault.core.model.FriendData
import com.wealthvault.domain.profile.ProfileRepository
import com.wealthvault.domain.social.AcceptFriendRequest
import com.wealthvault.domain.social.AcceptFriendResult
import com.wealthvault.domain.social.FriendProfile
import com.wealthvault.domain.social.MessageItem
import com.wealthvault.domain.social.PendingFriend
import com.wealthvault.domain.portfolio.AccountData as DomainAccountData
import com.wealthvault.core.model.BankAccountData as DomainBankAccountData
import com.wealthvault.core.model.BuildingIdData as DomainBuildingIdData
import com.wealthvault.core.model.CashIdData as DomainCashIdData
import com.wealthvault.domain.portfolio.GetBuildingData as DomainGetBuildingData
import com.wealthvault.domain.portfolio.GetCashData as DomainGetCashData
import com.wealthvault.domain.portfolio.GetInsuranceData as DomainGetInsuranceData
import com.wealthvault.domain.portfolio.GetInvestmentData as DomainGetInvestmentData
import com.wealthvault.domain.portfolio.GetLandData as DomainGetLandData
import com.wealthvault.domain.portfolio.GetLiabilityData as DomainGetLiabilityData
import com.wealthvault.core.model.InsuranceIdData as DomainInsuranceIdData
import com.wealthvault.core.model.InvestmentIdData as DomainInvestmentIdData
import com.wealthvault.core.model.LandIdData as DomainLandIdData
import com.wealthvault.core.model.LiabilityIdData as DomainLiabilityIdData
import com.wealthvault.domain.portfolio.PortfolioRepository
import com.wealthvault.domain.social.GroupChatAction
import com.wealthvault.domain.social.GroupData as DomainGroupData
import com.wealthvault.domain.social.GroupMember as DomainGroupMember
import com.wealthvault.domain.social.GroupMessage as DomainGroupMessage
import com.wealthvault.domain.social.GroupResult as DomainGroupResult
import com.wealthvault.domain.social.GroupSummary
import com.wealthvault.domain.social.GrantAccess
import com.wealthvault.domain.social.ShareableItem as DomainShareableItem
import com.wealthvault.domain.social.ShareFriend as DomainShareFriend
import com.wealthvault.domain.social.ShareGroup as DomainShareGroup
import com.wealthvault.domain.social.ShareItems

internal class SocialDataSource(
    private val profileRepository: ProfileRepository,
    private val groupApi: GetAllGroupApi,
    private val createGroupApi: CreateGroupApi,
    private val userTransport: SocialUserTransport,

    private val portfolioRepository: PortfolioRepository,
    private val getGroupMsgApi: GetGroupMsgApi,
    private val getShareFriendApi: GetShareFriendApi,
    private val getShareGroupApi: GetShareGroupApi,
    private val grantAccessApi: GrantAccessApi,
    private val getGroupApi: GetGroupApi,
    private val getGroupMemberApi: GetGroupMemberApi,
    private val updateGroupApi: UpdateGroupApi,
    private val addMemberApi: AddMemberApi,
    private val removeMemberApi: RemoveMemberApi,
    private val leaveGroupApi: LeaveGroupApi,
    private val getItemToShareApi: GetItemToShareApi,
    private val shareItemApi: ShareItemApi,
    private val unShareFriendApi: UnShareFriendApi,
    private val unShareGroupApi: UnShareGroupApi,
    private val deleteGroupApi: DeleteGroupApi,
    private val logger: AppLogger = platformLogger(),


    ) : SocialRemoteDataSource {
    override suspend fun getAllFriends(): AppResult<List<FriendData>> {
        return profileRepository.getAllFriends()
    }

    override suspend fun getAllGroups(): AppResult<List<GroupSummary>> {
        return runSuspendAppCatching {
            groupApi.getAllGroup()
        }
    }

    override suspend fun createGroup(
        groupName: String,
        memberIds: List<String>,
        imageBytes: ByteArray?
    ): AppResult<DomainGroupResult> {
        return runSuspendAppCatching {
            createGroupApi.createGroup(groupName, memberIds, imageBytes)
        }
    }

    // 🌟 2. เรียกใช้ API ค้นหาเพื่อน
    override suspend fun searchUser(email: String): AppResult<FriendData?> {
        return runSuspendAppCatching {
            val response = userTransport.searchUser(email)
            // ดึงคนแรกออกมา ถ้าไม่มีให้เป็น null
            response.firstOrNull()
        }
    }

    // 🌟 3. เรียกใช้ API เพิ่มเพื่อน
    override suspend fun addFriend(targetId: String): AppResult<Boolean> {
        return runSuspendAppCatching {
            userTransport.addFriend(targetId)
        }
    }
    override suspend fun getFriendMessages(friendId: String): AppResult<List<MessageItem>> {
        return runSuspendAppCatching {
            userTransport.getFriendMessages(friendId)
        }
    }
    override suspend fun getFriendProfile(friendId: String): AppResult<FriendProfile> {
        return runSuspendAppCatching {
            userTransport.getFriendProfile(friendId)
        }
    }

    override suspend fun getAccountById(id: String): AppResult<DomainBankAccountData?> =
        portfolioRepository.getAccountById(id, force = false).map { it }

    override suspend fun getBuildingById(id: String): AppResult<DomainBuildingIdData?> =
        portfolioRepository.getBuildingById(id, force = false).map { it }

    override suspend fun getCashById(id: String): AppResult<DomainCashIdData?> =
        portfolioRepository.getCashById(id, force = false).map { it }

    override suspend fun getInsuranceById(id: String): AppResult<DomainInsuranceIdData?> =
        portfolioRepository.getInsuranceById(id, force = false).map { it }

    override suspend fun getInvestmentById(id: String): AppResult<DomainInvestmentIdData?> =
        portfolioRepository.getInvestmentById(id, force = false).map { it }

    override suspend fun getLandById(id: String): AppResult<DomainLandIdData?> =
        portfolioRepository.getLandById(id, force = false).map { it }

    override suspend fun getLiabilityById(id: String): AppResult<DomainLiabilityIdData?> =
        portfolioRepository.getLiabilityById(id, force = false).map { it }

    override suspend fun getGroupMessages(groupId: String): AppResult<List<DomainGroupMessage>> {
        return runSuspendAppCatching {
            getGroupMsgApi.getGroupMsg(groupId)
        }
    }

    override suspend fun getShareFriendItems(friendId: String): AppResult<List<DomainShareFriend>> {
        return runSuspendAppCatching {
            getShareFriendApi.getShareFriend(friendId)
        }
    }
    override suspend fun getShareGroupItems(groupId: String): AppResult<List<DomainShareGroup>> {
        return runSuspendAppCatching {
            getShareGroupApi.getShareGroup(groupId)
        }
    }
    override suspend fun grantAccess(groupId: String, targetId: String, itemIds: List<String>): AppResult<Boolean> {
        return runSuspendAppCatching {
            grantAccessApi.grantAccess(
                groupId,
                com.wealthvault.domain.social.GrantAccess(targetId = targetId, itemIds = itemIds),
            )
        }
    }

    override suspend fun getGroupDetail(groupId: String): AppResult<DomainGroupData> {
        return runSuspendAppCatching {
            getGroupApi.getGroupDetail(groupId)
        }.toGroupDataResult()
    }
    override suspend fun getGroupMembers(groupId: String): AppResult<List<DomainGroupMember>> {
        return runSuspendAppCatching {
            getGroupMemberApi.getGroupMembers(groupId)
        }
    }
    override suspend fun updateGroup(groupId: String, groupName: String, profileImage: ByteArray?): AppResult<DomainGroupResult> {
        return runSuspendAppCatching {
            updateGroupApi.updateGroup(groupId, groupName, profileImage)
        }
    }

    override suspend fun addGroupMember(groupId: String, targetId: String): AppResult<Boolean> {
        return runSuspendAppCatching {
            addMemberApi.addMember(groupId, targetId)
        }
    }

    override suspend fun removeGroupMember(groupId: String, targetId: String): AppResult<Boolean> {
        return runSuspendAppCatching {
            removeMemberApi.removeMember(groupId, targetId)
        }
    }
    override suspend fun leaveGroup(groupId: String): AppResult<Boolean> {
        return runSuspendAppCatching {
            leaveGroupApi.leaveGroup(groupId)
        }
    }
    override suspend fun removeFriend(targetId: String): AppResult<Boolean> {
        return runSuspendAppCatching {
            userTransport.deleteFriend(targetId)
        }
    }
    // ใน SocialDataSource.kt

    override suspend fun getItemsToShare(targetId: String, isGroup: Boolean): AppResult<List<DomainShareableItem>> {
        return runSuspendAppCatching {
            // 🌟 แปลง Boolean เป็น String ที่ Backend ต้องการ
            val type = if (isGroup) "group" else "friend"

            getItemToShareApi.getItemsToShare(type, targetId)
        }
    }
    override suspend fun submitShareItems(request: ShareItems): AppResult<Boolean> {
        return runSuspendAppCatching {
            shareItemApi.shareItem(request)
        }
    }
    override suspend fun unShareFriendItem(id: String): AppResult<Boolean> = runSuspendAppCatching {
        unShareFriendApi.unShareFriend(id)
        true
    }

    // 🌟 ฟังก์ชันยกเลิกแชร์กลุ่ม
    override suspend fun unShareGroupItem(id: String): AppResult<Boolean> = runSuspendAppCatching {
        unShareGroupApi.unShareGroup(id)
        true
    }
    override suspend fun deleteGroup(groupId: String): AppResult<Boolean> = runSuspendAppCatching {
        deleteGroupApi.deleteGroup(groupId)
    }

    override suspend fun acceptFriend(request: AcceptFriendRequest): AppResult<AcceptFriendResult> {
        return runSuspendAppCatching {
            val success = userTransport.acceptFriend(request)
            logger.debug("Accept friend response received")
            AcceptFriendResult(success = success)
        }
    }

    override suspend fun getPendingFriends(): AppResult<List<PendingFriend>> {
        return runSuspendAppCatching {
            userTransport.getPendingFriends()
        }
    }
}

/**
 * A successful transport envelope can still contain no group data when the
 * group was deleted between navigation and the detail request. Keep that
 * case in the shared error vocabulary instead of throwing an untyped
 * exception that presentation cannot distinguish from an outage.
 */
internal fun AppResult<DomainGroupResult>.toGroupDataResult(): AppResult<DomainGroupData> = when (this) {
    is AppResult.Success -> value.data?.let { AppResult.Success(it) } ?: AppResult.Failure(AppError.NotFound)
    is AppResult.Failure -> this
}

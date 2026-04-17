package com.wealthvault.financiallist.ui.shareasset.model


data class GroupTargetModel(
    val groupId: String,
    val groupName: String,
    val isShared: Boolean
)

// ข้อมูลเพื่อนพร้อมสถานะการแชร์
data class FriendTargetModel(
    val friendId: String,
    val friendName: String,
    val isShared: Boolean
)

// มัดรวมกันเพื่อส่งกลับไปให้ ViewModel
data class ShareCombinedData(
    val mappedGroups: List<GroupTargetModel>,
    val mappedFriends: List<FriendTargetModel>
)

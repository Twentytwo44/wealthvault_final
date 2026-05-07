package com.wealthvault.financiallist.ui.shareasset.model

data class GroupTargetModel(
    val groupId: String,
    val groupName: String,
    val memberCount: Int,         // 🌟 เพิ่มตัวแปรรับจำนวนสมาชิก
    val groupProfile: String?,    // 🌟 เพิ่มตัวแปรรับ URL รูปกลุ่ม (ใส่ ? เพราะอาจจะไม่มีรูป)
    val isShared: Boolean,
    val sharedAt: String? = null
)

// ข้อมูลเพื่อนพร้อมสถานะการแชร์
data class FriendTargetModel(
    val friendId: String,
    val friendName: String,
    val email: String,            // 🌟 เพิ่มตัวแปรรับอีเมล
    val profile: String?,         // 🌟 เพิ่มตัวแปรรับ URL รูปโปรไฟล์ (ใส่ ? เพราะอาจจะไม่มีรูป)
    val isShared: Boolean,
    val sharedAt: String? = null
)

// มัดรวมกันเพื่อส่งกลับไปให้ ViewModel
data class ShareCombinedData(
    val mappedGroups: List<GroupTargetModel>,
    val mappedFriends: List<FriendTargetModel>
)
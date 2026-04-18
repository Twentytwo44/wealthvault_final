package com.wealthvault.social

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

// Import หน้าจอทั้งหมดที่เราเพิ่งสร้างไป
import com.wealthvault.social.ui.SocialScreen
import com.wealthvault.social.ui.space.FriendSpaceScreen
import com.wealthvault.social.ui.space.GroupSpaceScreen
import com.wealthvault.social.ui.share.AssetShareScreen
import com.wealthvault.social.ui.manage_shared.SharedAssetManageScreen
// Import หน้าโปรไฟล์ที่เพิ่งสร้างใหม่
import com.wealthvault.social.ui.profile.FriendProfileScreen
import com.wealthvault.social.ui.profile.GroupProfileScreen

// 1. พรีวิวหน้า Social หลัก (สลับแท็บเพื่อน/กลุ่ม)
@Preview(showBackground = true, name = "1. Social Main Screen", device = "id:pixel_7")
@Composable
fun SocialScreenPreview() {
    SocialScreen()
}

// 2. พรีวิวหน้า Friend Space
//@Preview(showBackground = true, name = "2. Friend Space (Twentytwo)", device = "id:pixel_7")
//@Composable
//fun FriendSpaceScreenPreview() {
//    FriendSpaceScreen(
//        friendName = "Twentytwo",
//        onBackClick = {},
//        onShareClick = {},
//        onManageClick = {}
//    )
//}

// 3. พรีวิวหน้า Group Space
//@Preview(showBackground = true, name = "3. Group Space (Family)", device = "id:pixel_7")
//@Composable
//fun GroupSpaceScreenPreview() {
//    GroupSpaceScreen(
//        groupName = "Family",
//        onBackClick = {},
//        onShareClick = {},
//        onManageClick = {}
//    )
//}

// 4. พรีวิวหน้าเลือกทรัพย์สินเพื่อแชร์
//@Preview(showBackground = true, name = "4. Asset Share Screen", device = "id:pixel_7")
//@Composable
//fun AssetShareScreenPreview() {
//    AssetShareScreen(
//        targetName = "Twentytwo",
//        onBackClick = {},
//        onShareConfirm = {}
//    )
//}
//
//// 5. พรีวิวหน้าจัดการทรัพย์สินที่แชร์
//@Preview(showBackground = true, name = "5. Shared Manage Screen", device = "id:pixel_7")
//@Composable
//fun SharedAssetManageScreenPreview() {
//    SharedAssetManageScreen(
//        targetName = "Twentytwo", // ลองเปลี่ยนชื่อตรงนี้ดู ข้อความในหน้าจอจะเปลี่ยนตามครับ
//        onBackClick = {}
//    )
//}

//// 6. พรีวิวหน้าโปรไฟล์เพื่อน
//@Preview(showBackground = true, name = "6. Friend Profile Screen", device = "id:pixel_7")
//@Composable
//fun FriendProfileScreenPreview() {
//    FriendProfileScreen(
//        onBackClick = {},
//        onRemoveFriendClick = {}
//    )
//}

// 7. พรีวิวหน้าโปรไฟล์กลุ่ม
//@Preview(showBackground = true, name = "7. Group Profile Screen", device = "id:pixel_7")
//@Composable
//fun GroupProfileScreenPreview() {
//    GroupProfileScreen(
//        onBackClick = {},
//        onLeaveGroupClick = {},
//        onMembersClick = {},
//        onAddMemberClick = {}
//    )
//}
package com.wealthvault.social.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.group_api.model.GroupData
import com.wealthvault.group_api.model.GroupMemberItem
import com.wealthvault.social.ui.components.FriendListItem

// Import component
import com.wealthvault.social.ui.components.space.SpaceTopBar
import com.wealthvault.social.ui.components.profile.ProfileHeader
import com.wealthvault.social.ui.main_social.create_group.EditGroupScreen
import androidx.compose.runtime.getValue  // 🌟 สำหรับอ่านค่า (getter)
import androidx.compose.runtime.setValue  // 🌟 สำหรับเขียนค่า (setter) - ตัวนี้แหละที่หายไป
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.RedErr
import com.wealthvault.social.ui.SocialScreen
import com.wealthvault.data_store.TokenStore // 🌟 เพิ่ม import
import kotlinx.coroutines.flow.firstOrNull
import org.koin.compose.koinInject

// ==========================================
// 🌟 1. ส่วน Screen (จัดการ Logic และ State)
// ==========================================
class GroupProfileScreen(
    private val groupId: String
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val tokenStore = koinInject<TokenStore>() // 🌟 Inject TokenStore

        var currentUserId by remember { mutableStateOf<String?>(null) }

        // 🌟 อ่าน User ID จริงจากเครื่อง
        LaunchedEffect(Unit) {
            currentUserId = tokenStore.getUserId.firstOrNull()
        }

        var rootNavigator = navigator
        while (rootNavigator.parent != null) {
            rootNavigator = rootNavigator.parent!!
        }

        val screenModel = getScreenModel<GroupProfileScreenModel>()
        val leaveSuccess by screenModel.leaveSuccess.collectAsState()
        val groupData by screenModel.groupData.collectAsState()
        val isLoading by screenModel.isLoading.collectAsState()
        val members by screenModel.members.collectAsState()
        var showLeaveDialog by remember { mutableStateOf(false) }

        LaunchedEffect(groupId) {
            screenModel.fetchGroupData(groupId)
        }

        LaunchedEffect(leaveSuccess) {
            if (leaveSuccess) {
                navigator.popUntil { screen -> screen is SocialScreen }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            GroupProfileContent(
                groupData = groupData,
                members = members,
                isLoading = isLoading,
                currentUserId = currentUserId ?: "", // 🌟 ส่ง ID จริงไปแทน mocID
                onBackClick = { navigator.pop() },
                onFriendClick = { friendId, friendName ->
                    rootNavigator.push(FriendProfileScreen(friendId, friendName))
                },
                onEditGroupClick = {
                    rootNavigator.push(
                        EditGroupScreen(
                            groupId = groupId,
                            initialGroupName = groupData?.groupName ?: "",
                            initialImageUrl = groupData?.groupProfile,
                            initialMemberIds = members.mapNotNull { it.id }
                        )
                    )
                },
                onLeaveGroupClick = { showLeaveDialog = true }
            )

            // ... (AlertDialog ส่วนเดิม) ...
            if (showLeaveDialog) {
                AlertDialog(
                    onDismissRequest = { showLeaveDialog = false },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    title = { Text("ออกจากกลุ่ม?", fontWeight = FontWeight.Bold, color = Color(0xFF3A2F2A)) },
                    text = { Text("คุณแน่ใจหรือไม่ว่าต้องการออกจากกลุ่ม '${groupData?.groupName}'? คุณจะไม่สามารถเห็นข้อความในกลุ่มนี้ได้อีก", color = Color.Gray) },
                    confirmButton = {
                        TextButton(onClick = {
                            showLeaveDialog = false
                            screenModel.leaveGroup(groupId)
                        }) {
                            Text("ออกจากกลุ่ม", color = Color(0xFFE55A5A), fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLeaveDialog = false }) {
                            Text("ยกเลิก", color = Color.Gray)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GroupProfileContent(
    groupData: GroupData?,
    members: List<GroupMemberItem>,
    isLoading: Boolean,
    currentUserId: String, // 🌟 เปลี่ยนชื่อจาก mocID เป็น currentUserId
    onBackClick: () -> Unit,
    onFriendClick: (String, String) -> Unit,
    onEditGroupClick: () -> Unit,
    onLeaveGroupClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)
    // ... (ส่วนหัวและ ProfileHeader เดิม) ...
    Column(
        modifier = Modifier.fillMaxSize().background(LightBg).statusBarsPadding().padding(top = 24.dp)
    ) {
        SpaceTopBar(title = "โปรไฟล์กลุ่ม", onBackClick = onBackClick)
        HorizontalDivider(color = themeColor.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = themeColor)
            }
        } else {
            ProfileHeader(name = groupData?.groupName ?: "กำลังโหลด...", subtitle = "", profileImageUrl = groupData?.groupProfile)
        }

        val memberCountText = groupData?.memberCount?.let { " ($it)" } ?: ""
        Text(text = "สมาชิก$memberCountText", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 24.dp))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(members) { member ->
                val rawName = listOfNotNull(member.firstName, member.lastName).joinToString(" ").ifBlank { member.username ?: "ไม่ระบุชื่อ" }

                // 🌟 ใช้ currentUserId เช็คว่าเป็นตัวเองไหม
                val displayName = if (member.id == currentUserId) "$rawName (คุณ)" else rawName
                val isGroupLeader = member.id == groupData?.createdBy

                FriendListItem(
                    member = member,
                    isLeader = isGroupLeader,
                    onClick = {
                        member.id?.let { safeId ->
                            if (safeId != currentUserId) { onFriendClick(safeId, rawName) }
                        }
                    }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, top = 16.dp).navigationBarsPadding(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isLoading && groupData != null) {
                // 🌟 เช็คว่าเป็นเจ้าของกลุ่ม (Leader) หรือไม่
                if (groupData.createdBy == currentUserId) {
                    Text(
                        text = "แก้ไขกลุ่ม",
                        color = LightPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onEditGroupClick() }.padding(8.dp)
                    )
                } else {
                    Text(
                        text = "ออกจากกลุ่ม",
                        color = RedErr,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onLeaveGroupClick() }.padding(8.dp)
                    )
                }
            }
        }
    }
}
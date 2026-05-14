package com.wealthvault.social.ui.profile

// Import component
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.RedErr
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.data_store.TokenStore
import com.wealthvault.group_api.model.GroupData
import com.wealthvault.group_api.model.GroupMemberItem
import com.wealthvault.social.ui.SocialScreen
import com.wealthvault.social.ui.components.FriendListItem
import com.wealthvault.social.ui.components.profile.ProfileHeader
import com.wealthvault.social.ui.components.space.SpaceTopBar
import com.wealthvault.social.ui.main_social.create_group.EditGroupScreen
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
        val tokenStore = koinInject<TokenStore>() // 🌟 Inject TokenStore ถูกต้องแล้วครับ

        var currentUserId by remember { mutableStateOf<String?>(null) }

        // 🌟 อ่าน User ID จริงจากเครื่อง (ใช้ LaunchedEffect(Unit) ตรงนี้ถูกต้องแล้ว เพราะอ่านแค่ครั้งเดียวตอนสร้างหน้าจอ)
        LaunchedEffect(Unit) {
            currentUserId = tokenStore.getUserId.firstOrNull()
        }

        var rootNavigator = navigator
        while (rootNavigator.parent != null) {
            rootNavigator = rootNavigator.parent!!
        }

        val screenModel = getScreenModel<GroupProfileScreenModel>()
        val leaveSuccess by screenModel.leaveSuccess.collectAsStateWithLifecycle()
        val groupData by screenModel.groupData.collectAsStateWithLifecycle()
        val isLoading by screenModel.isLoading.collectAsStateWithLifecycle()
        val members by screenModel.members.collectAsStateWithLifecycle()
        var showLeaveDialog by remember { mutableStateOf(false) }

        // 🌟 1. ดึง Lifecycle มา
        val lifecycleOwner = LocalLifecycleOwner.current

        // 🌟 2. ลบ LaunchedEffect(groupId) ทิ้ง และใช้ ON_RESUME แทน
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    println("🔄 GroupProfile ตื่นแล้ว! สั่งโหลดข้อมูลกลุ่ม $groupId ใหม่...")
                    // ทุกครั้งที่สลับหน้ากลับมาจากการแก้ไขกลุ่ม หรือสลับแอป ข้อมูลจะอัปเดตเสมอ!
                    screenModel.fetchGroupData(groupId)
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        // 🌟 ส่วนนี้เก็บไว้เหมือนเดิม ถูกต้องแล้วครับ
        LaunchedEffect(leaveSuccess) {
            if (leaveSuccess) {
                // แนะนำเพิ่มเติม: ควรเรียก screenModel.resetLeaveState() ก่อน pop
                // เพื่อเคลียร์สถานะเป็น false ป้องกันบัคตอนเข้ากลุ่มอื่น
                navigator.popUntil { screen -> screen is SocialScreen }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            GroupProfileContent(
                groupData = groupData,
                members = members,
                isLoading = isLoading,
                currentUserId = currentUserId ?: "",
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

            // ... (AlertDialog ส่วนเดิมของคุณ คงไว้ได้เลยครับ ทำมาดีแล้ว) ...
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
        Spacer(modifier = Modifier.height(26.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = themeColor)
            }
        } else {
            ProfileHeader(name = groupData?.groupName ?: "กำลังโหลด...", profileImageUrl = groupData?.groupProfile)
        }

        val memberCountText = groupData?.memberCount?.let { " ($it)" } ?: ""
        Text(text = "สมาชิก$memberCountText", color = Color.Gray, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 24.dp))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(members) { member ->
                val rawName = listOfNotNull(member.firstName, member.lastName)
                    .joinToString(" ")
                    .ifBlank { member.username?.takeIf { it.isNotBlank() }
                    ?: member.firstName?.takeIf { it.isNotBlank() }
                    ?: "ไม่ระบุชื่อ" }

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
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onEditGroupClick() }.padding(8.dp)
                    )
                } else {
                    Text(
                        text = "ออกจากกลุ่ม",
                        color = RedErr,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onLeaveGroupClick() }.padding(8.dp)
                    )
                }
            }
        }
    }
}

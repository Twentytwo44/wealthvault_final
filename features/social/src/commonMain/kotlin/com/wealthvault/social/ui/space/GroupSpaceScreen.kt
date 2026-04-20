package com.wealthvault.social.ui.space

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.WealthVaultTheme
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.group_api.model.GroupMsgData
import kotlinx.coroutines.delay

import com.wealthvault.social.ui.components.space.ActivityBubbleCard
import com.wealthvault.social.ui.components.space.SpaceFloatingMenu
import com.wealthvault.social.ui.components.space.SpaceTopBar
import com.wealthvault.social.ui.components.profile.SmartAssetDetailDialog
import com.wealthvault.social.ui.components.space.InlineGrantAccessCard
import com.wealthvault.social.ui.components.space.SystemAlertBubble
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.shape.CircleShape
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_down_line
import com.wealthvault.core.generated.resources.ic_social_manage
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.social.ui.manage_shared.SharedAssetManageScreen
import com.wealthvault.social.ui.manage_shared.SharedAssetScreen
import com.wealthvault.social.ui.profile.FriendProfileScreen
import com.wealthvault.social.ui.profile.GroupProfileScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

class GroupSpaceScreen(
    private val groupId: String,
    private val groupName: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var rootNavigator = navigator
        while (rootNavigator.parent != null) {
            rootNavigator = rootNavigator.parent!!
        }

        val screenModel = getScreenModel<GroupSpaceScreenModel>()

        val messages by screenModel.messages.collectAsState()
        val isLoading by screenModel.isLoading.collectAsState()

        val reversedMessages = remember(messages) { messages.reversed() }

        LaunchedEffect(groupId) {
            screenModel.fetchMessages(groupId)
        }


        WealthVaultTheme {
            GroupSpaceContent(
                groupId = groupId, // 🌟 ส่ง groupId เข้ามาด้วย
                groupName = groupName,
                messages = messages,
                isLoading = isLoading,
                onBackClick = { navigator.pop() },
                onShareClick = {
                    navigator.push(
                        SharedAssetScreen(
                            targetId = groupId,
                            targetName = groupName,
                            isGroup = true
                        )
                    )
                },
                onManageClick = {
                    navigator.push(
                        SharedAssetManageScreen(
                            targetId = groupId,
                            targetName = groupName,
                            isGroup = true
                        )
                    )
                },
                onMoreClick = {
                    rootNavigator.push(
                        GroupProfileScreen(
                            groupId = groupId
                        )
                    )
                },
                onGrantAccess = { targetId, itemIds ->
                    screenModel.grantAccess(groupId, targetId, itemIds)
                }
            )
        }
    }
}

@Composable
fun GroupSpaceContent(
    groupId: String,
    groupName: String,
    messages: List<GroupMsgData>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onManageClick: () -> Unit,
    onMoreClick: () -> Unit,
    onGrantAccess: (String, List<String>) -> Unit
) {
    val themeColor = Color(0xFFC27A5A)
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var selectedAssetId by remember { mutableStateOf<String?>(null) }
    var selectedAssetType by remember { mutableStateOf<String?>(null) }

    // 🌟 เช็คว่าตอนนี้เลื่อนจอขึ้นไปจากจุดล่างสุด (index 0) หรือยัง
    val showScrollToBottom by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    // 🌟 Box นอกสุด (Root)
    Box(modifier = Modifier.fillMaxSize().background(LightBg).statusBarsPadding()) {

        // --- ส่วนที่ 1: แชทและ TopBar ---
        Column(modifier = Modifier.fillMaxSize().padding(top = 24.dp)) {
            SpaceTopBar(title = groupName, onBackClick = onBackClick, showMoreOption = true, onMoreClick = onMoreClick)
            HorizontalDivider(color = themeColor.copy(alpha = 0.3f), thickness = 1.dp)

            if (isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = themeColor)
                }
            } else if (messages.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "ยังไม่มีความเคลื่อนไหวในกลุ่ม $groupName",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    reverseLayout = true, // 🌟 ลิสต์จากล่างขึ้นบน
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 300.dp)
                ) {
                    items(messages) { msg ->
                        when (msg.msgType) {

                            // 🌟 เคสที่ 1: แจ้งเตือนระบบธรรมดา (เพื่อนเข้ากลุ่ม, บลาๆ)
                            "SYSTEM_ALERT" -> {
                                SystemAlertBubble(content = msg.content ?: "แจ้งเตือนระบบ")
                            }

                            // 🌟 เคสที่ 2: การ์ดมอบสิทธิ์ (แยกออกมาจาก SYSTEM_ALERT แล้ว!)
                            "GRANT_ACCESS" -> {
                                val meta = msg.metadata
                                val isGrantAccessPrompt = meta?.type == "GRANT_ACCESS_PROMPT"

                                if (isGrantAccessPrompt) {
                                    if (meta?.isCompleted == false) {

                                        // ดึงชื่อเพื่อนออกมาจาก content
                                        val contentStr = msg.content ?: ""
                                        val extractedName = when {
                                            contentStr.contains("สมาชิกใหม่: ") ->
                                                contentStr.substringAfter("สมาชิกใหม่: ").substringBefore(" เข้ากลุ่ม")
                                            contentStr.contains("ให้ ") && contentStr.contains(" หรือไม่") ->
                                                contentStr.substringAfter("ให้ ").substringBefore(" หรือไม่")
                                            else -> "สมาชิก"
                                        }

                                        InlineGrantAccessCard(
                                            groupId = groupId,
                                            targetName = extractedName.trim(),
                                            targetUserId = meta.targetUserId ?: "",
                                            themeColor = themeColor,
                                            onSaveSuccess = { selectedIds ->
                                                onGrantAccess(meta.targetUserId ?: "", selectedIds)
                                            }
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "คุณได้จัดการการแชร์ทรัพย์สินเรียบร้อยแล้ว",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = Color.Gray,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }

                            // 🌟 เคสที่ 3: การ์ดแสดงทรัพย์สินที่ถูกแชร์
                            "ASSET_CARD" -> {
                                val isMe = msg.isMe == true
                                val senderName = if (isMe) "คุณ" else (msg.senderName ?: "สมาชิก")
                                val titleText = "$senderName ได้แชร์ทรัพย์สินนี้ลงในกลุ่ม"

                                ActivityBubbleCard(
                                    title = titleText,
                                    // ดึง itemName ก่อน ถ้าไม่มีก็ดึง snapshotTitle
                                    assetName = msg.metadata?.itemName ?: msg.metadata?.snapshotTitle ?: "ไม่ระบุชื่อทรัพย์สิน",
                                    assetType = msg.metadata?.assetType ?: "ไม่ระบุประเภท",
                                    isMe = isMe,
                                    profileImageUrl = msg.senderImage,

                                    // 🌟 3. โยนสถานะการลบเข้า Component
                                    isDeleted = msg.metadata?.isDeleted == true,

                                    themeColor = themeColor,
                                    onDetailClick = {
                                        selectedAssetId = msg.metadata?.assetId
                                        selectedAssetType = msg.metadata?.assetType
                                    }
                                )

                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        } // ปิด Column

        // --- ส่วนที่ 2: ปุ่มลอยเลื่อนลงล่างสุด ---
        AnimatedVisibility(
            visible = showScrollToBottom,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            SmallFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                containerColor = Color.White,
                contentColor = themeColor,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_common_down_line),
                    contentDescription = "เลื่อนลงล่างสุด",
                    tint = LightPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // --- ส่วนที่ 3: เมนูลอย Floating Menu ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 26.dp, end = 16.dp)
        ) {
            SpaceFloatingMenu(
                onShareClick = onShareClick,
                onManageClick = onManageClick,
            )
        }

        // --- ส่วนที่ 4: Dialogs ---
        if (selectedAssetId != null && selectedAssetType != null) {
            SmartAssetDetailDialog(
                assetId = selectedAssetId!!,
                assetType = selectedAssetType!!,
                showBottomMenu = false,
                onDismiss = {
                    selectedAssetId = null
                    selectedAssetType = null
                }
            )
        }
    }
}
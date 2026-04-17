package com.wealthvault.social.ui.space

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_down_line
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.WealthVaultTheme
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.social.ui.components.space.ActivityBubbleCard
import com.wealthvault.social.ui.components.space.SpaceFloatingMenu
import com.wealthvault.social.ui.components.space.SpaceTopBar
import com.wealthvault.social.ui.profile.FriendProfileScreen
import com.wealthvault.`user-api`.model.MessageItem
import kotlinx.coroutines.launch

// 🌟 Import Smart Dialog มาใช้งาน
import com.wealthvault.social.ui.components.profile.SmartAssetDetailDialog
import com.wealthvault.social.ui.manage_shared.SharedAssetManageScreen
import com.wealthvault.social.ui.manage_shared.SharedAssetScreen
import org.jetbrains.compose.resources.painterResource

class FriendSpaceScreen(
    private val friendId: String,
    private val friendName: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var rootNavigator = navigator
        while (rootNavigator.parent != null) {
            rootNavigator = rootNavigator.parent!!
        }
        val screenModel = getScreenModel<FriendSpaceScreenModel>()

        val messages by screenModel.messages.collectAsState()
        // 🌟 กลับด้าน List เหมือนเดิม เพื่อให้ใช้ร่วมกับ reverseLayout ได้
        val reversedMessages = remember(messages) { messages.reversed() }
        val isLoading by screenModel.isLoading.collectAsState()

        LaunchedEffect(friendId) {
            screenModel.fetchMessages(friendId)
        }

        WealthVaultTheme {
            FriendSpaceContent(
                friendName = friendName,
                messages = reversedMessages,
                isLoading = isLoading,
                onBackClick = { navigator.pop() },
                onShareClick = {
                    navigator.push(
                        SharedAssetScreen(
                            targetId = friendId,        // ส่ง ID เพื่อนไป
                            targetName = friendName,    // ส่งชื่อเพื่อนไป
                            isGroup = false             // บอกว่าไม่ใช่กลุ่มนะ
                        )
                    )
                               },
                onManageClick = {
                    navigator.push(
                        SharedAssetManageScreen(
                            targetId = friendId,        // ส่ง ID เพื่อนไป
                            targetName = friendName,    // ส่งชื่อเพื่อนไป
                            isGroup = false             // บอกว่าไม่ใช่กลุ่มนะ
                        )
                    )
                },
                onMoreClick = {
                    rootNavigator.push(
                        FriendProfileScreen(
                            friendId = friendId,
                            friendName = friendName
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun FriendSpaceContent(
    friendName: String,
    messages: List<MessageItem>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onManageClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    val themeColor = Color(0xFFC27A5A)

    // 🌟 ประกาศ state และ coroutine สำหรับควบคุมการเลื่อน
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var selectedAssetId by remember { mutableStateOf<String?>(null) }
    var selectedAssetType by remember { mutableStateOf<String?>(null) }

    // 🌟 เช็คว่ามีการไถจอขึ้นไปหรือยัง (เพื่อโชว์ปุ่มเลื่อนลงล่างสุด)
    val showScrollToBottom by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    // Box หลัก นอกสุด
    Box(modifier = Modifier.fillMaxSize().background(LightBg).statusBarsPadding()) {

        // --- ส่วนที่ 1: TopBar และ แชท ---
        Column(modifier = Modifier.fillMaxSize().padding(top = 24.dp)) {
            SpaceTopBar(title = friendName, onBackClick = onBackClick, showMoreOption = true, onMoreClick = onMoreClick)

            HorizontalDivider(color = themeColor.copy(alpha = 0.3f), thickness = 1.dp)

            if (isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = themeColor)
                }
            } else if (messages.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "ยังไม่มีความเคลื่อนไหวระหว่างคุณกับ $friendName",
                        color = Color.Gray,
                        modifier = Modifier.padding(32.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    reverseLayout = true, // 🌟 สั่งให้โหลดจากล่างขึ้นบน
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 300.dp)
                ) {
                    items(messages) { msg ->
                        val isMe = msg.isMe == true
                        val titleText = if (isMe) "คุณได้แชร์ทรัพย์สินนี้กับ $friendName" else "$friendName ได้แชร์ทรัพย์สินนี้กับคุณ"

                        ActivityBubbleCard(
                            title = titleText,
                            assetName = msg.metadata?.itemName ?: "ไม่ระบุชื่อทรัพย์สิน",
                            assetType = msg.metadata?.assetType ?: "ไม่ระบุประเภท",
                            isMe = isMe,
                            profileImageUrl = msg.senderImage,
                            themeColor = themeColor,
                            onDetailClick = {
                                selectedAssetId = msg.metadata?.assetId
                                selectedAssetType = msg.metadata?.assetType
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        } // ปิด Column

        // --- ส่วนที่ 2: ปุ่มลอยเลื่อนลงล่างสุด (อยู่ข้างนอก Column จะได้ไม่ติดเรื่อง Scope) ---
        AnimatedVisibility(
            visible = showScrollToBottom,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp) // ดันพ้นเมนูลอย
        ) {
            SmallFloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0) // กดปุ๊บ เลื่อนลงล่างสุด
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
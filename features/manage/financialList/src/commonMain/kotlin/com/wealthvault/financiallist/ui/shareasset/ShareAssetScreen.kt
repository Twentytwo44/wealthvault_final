package com.wealthvault.financiallist.ui.shareasset

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.generated.resources.ic_form_check
import com.wealthvault.core.theme.*
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.financiallist.ui.shareasset.component.AddEmailContent
import com.wealthvault.financiallist.ui.shareasset.component.FriendSelectionList
import com.wealthvault.financiallist.ui.shareasset.component.ShareItemWithDelete
import com.wealthvault.financiallist.ui.shareasset.model.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun CustomCheckbox(isSelected: Boolean, onSelectedChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .size(22.dp) // 🌟 ปรับขนาดให้มาตรฐาน
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) LightPrimary else Color.Transparent)
            .border(
                width = 2.dp,
                color = if (isSelected) LightPrimary else Color.LightGray.copy(0.5f),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable { onSelectedChange(!isSelected) },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                painter = painterResource(Res.drawable.ic_form_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

data class ShareAssetScreen(val type: String, val id: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<ShareScreenModel>()
        val friendState by screenModel.friendState.collectAsState()
        val groupState by screenModel.groupState.collectAsState()

        LaunchedEffect(Unit) { screenModel.initData(id, type) }

        ShareAssetContent(
            onBackClick = { navigator.pop() },
            onPrepareUnshare = { itemToUnshare, onReadyCallback ->
                screenModel.prepareUnshareItem(itemToUnshare, id) { preparedItem -> onReadyCallback(preparedItem) }
            },
            onNextClick = { shareTo, unshareList ->
                screenModel.initShareData(shareTo)
                screenModel.submitShare(id = id, type = type, unshareList = unshareList, onSuccess = { navigator.pop() })
            },
            friendData = friendState,
            groupData = groupState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareAssetContent(
    onBackClick: () -> Unit = {},
    onPrepareUnshare: (ShareInfo, (ShareInfo) -> Unit) -> Unit = { _, _ -> },
    onNextClick: (ShareTo, List<ShareInfo>) -> Unit = { _, _ -> },
    friendData: List<FriendTargetModel> = emptyList(),
    groupData: List<GroupTargetModel> = emptyList(),
) {
    val selectedFriends = remember { mutableStateListOf<ShareInfo>() }
    val selectedEmails = remember { mutableStateListOf<ShareInfo>() }
    val itemsToUnshare = remember { mutableStateListOf<ShareInfo>() }

    LaunchedEffect(friendData, groupData) {
        val preSelected = mutableListOf<ShareInfo>()

        friendData.filter { it.isShared }.forEach { friend ->
            preSelected.add(
                ShareInfo(
                    name = friend.friendName,
                    userId = friend.friendId,
                    typeData = "F",
                    subText = friend.email ?: "", // กันค่า null
                    profileUrl = friend.profile,
                    isShared = true,
                    date = friend.sharedAt
                )
            )
        }

        groupData.filter { it.isShared }.forEach { group ->
            preSelected.add(
                ShareInfo(
                    name = group.groupName,
                    userId = group.groupId,
                    typeData = "G",
                    subText = "${group.memberCount}",
                    profileUrl = group.groupProfile,
                    isShared = true,
                    date = group.sharedAt
                )
            )
        }

        preSelected.forEach { item ->
            if (selectedFriends.none { it.userId == item.userId }) selectedFriends.add(item)
        }
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showEmailSheet by remember { mutableStateOf(false) }
    var showEmailInfoTooltip by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 12.dp, top = 20.dp)
                ) {
                    Icon(painterResource(Res.drawable.ic_common_back), null, tint = LightPrimary, modifier = Modifier.size(24.dp).clickable { onBackClick() })
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("แชร์ทรัพย์สิน", style = MaterialTheme.typography.titleLarge, color = LightPrimary)
                        Text("คุณต้องการให้ใครเห็นทรัพย์สินนี้บ้าง?", style = MaterialTheme.typography.bodyMedium, color = LightPrimary.copy(alpha = 0.7f))
                    }
                }
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = {
                        val dataToSend = ShareTo(
                            friend = selectedFriends.filter { it.typeData == "F" },
                            email = selectedEmails.toList(),
                            group = selectedFriends.filter { it.typeData == "G" },
                        )
                        onNextClick(dataToSend, itemsToUnshare.toList())
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp), // 🌟 ปรับสูง 46.dp
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
                ) {
                    Text("ยืนยัน", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp)) {

            SectionHeader(title = "เลือกเพื่อนหรือกลุ่มที่ต้องการแชร์", onAddClick = { showBottomSheet = true })

            Card(
                modifier = Modifier.fillMaxWidth().height(260.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, LightBorder.copy(0.5f)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                if (selectedFriends.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("ยังไม่ได้เลือกเพื่อนหรือกลุ่ม", color = Color.Gray.copy(0.6f), style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    LazyColumn(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(selectedFriends) { friend ->
                            ShareItemWithDelete(data = friend, onDelete = {
                                if (friend.isShared == true) onPrepareUnshare(friend) { itemsToUnshare.add(it) }
                                selectedFriends.remove(friend)
                            })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Column {
                    SectionHeader(
                        title = "แชร์ให้คนที่ไม่มีบัญชี",
                        showInfo = true,
                        onInfoClick = { showEmailInfoTooltip = !showEmailInfoTooltip },
                        onAddClick = { showEmailSheet = true }
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, LightBorder.copy(0.5f)),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        if (selectedEmails.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("ยังไม่มีการเพิ่มอีเมล", color = Color.Gray.copy(0.6f), style = MaterialTheme.typography.bodyMedium)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(selectedEmails) { email ->
                                    ShareItemWithDelete(data = email, onDelete = {
                                        if (email.isShared == true) itemsToUnshare.add(email)
                                        selectedEmails.remove(email)
                                    })
                                }
                            }
                        }
                    }
                }

                // 🌟 Tooltip: ย้ายมาไว้นอก Column แต่อยู่ใน Box เดียวกัน
                androidx.compose.animation.AnimatedVisibility(
                    visible = showEmailInfoTooltip,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter) // ตอนนี้จะใช้ TopCenter ได้แล้วเพราะอยู่ใน Box
                        .zIndex(5f)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .offset(y = 42.dp)
                            .shadow(12.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, LightPrimary.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.clickable { showEmailInfoTooltip = false }.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, null, tint = LightPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "หากผู้รับยังไม่มีบัญชี Wealth & Vault ระบบจะส่งคำเชิญให้ผ่านอีเมลเพื่อให้เข้าถึงข้อมูลนี้ได้",
                                color = LightText.copy(0.8f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        // --- Sheets ---
        if (showBottomSheet) {
            ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState, containerColor = LightBg) {
                FriendSelectionList(
                    alreadySelected = selectedFriends.toList(), friendData = friendData, groupData = groupData,
                    onConfirm = { items ->
                        val newItems = items.filter { newItem -> selectedFriends.none { it.userId == newItem.userId } }
                        selectedFriends.addAll(newItems)
                        scope.launch { sheetState.hide() }.invokeOnCompletion { if (!sheetState.isVisible) showBottomSheet = false }
                    }
                )
            }
        }

        if (showEmailSheet) {
            ModalBottomSheet(onDismissRequest = { showEmailSheet = false }, containerColor = LightBg) {
                AddEmailContent(
                    onConfirm = { email, date, apiDate ->
                        if (email.isNotBlank() && !selectedEmails.any { it.userId == email }) {
                            selectedEmails.add(ShareInfo(name = email, userId = email, date = date, apiDate = apiDate, typeData = "E", subText = email))
                        }
                        showEmailSheet = false
                    }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, showInfo: Boolean = false, onInfoClick: () -> Unit = {}, onAddClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = LightPrimary, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            if (showInfo) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.Info, null, modifier = Modifier.size(20.dp).clickable { onInfoClick() }, tint = LightPrimary.copy(alpha = 0.6f))
            }
        }
        IconButton(onClick = onAddClick) { Icon(painterResource(Res.drawable.ic_common_plus), null, tint = LightPrimary, modifier = Modifier.size(24.dp)) }
    }
}
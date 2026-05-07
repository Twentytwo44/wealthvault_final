package com.wealthvault.financiallist.ui.shareasset

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.generated.resources.ic_form_check
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.financiallist.ui.shareasset.component.AddEmailContent
import com.wealthvault.financiallist.ui.shareasset.component.FriendSelectionList
import com.wealthvault.financiallist.ui.shareasset.component.ShareItemWithDelete
import com.wealthvault.financiallist.ui.shareasset.model.FriendTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.GroupTargetModel
import com.wealthvault.financiallist.ui.shareasset.model.ShareInfo
import com.wealthvault.financiallist.ui.shareasset.model.ShareTo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun CustomCheckbox(isSelected: Boolean, onSelectedChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) LightPrimary else Color.Transparent)
            .border(
                width = 2.dp,
                color = if (isSelected) LightPrimary else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onSelectedChange(!isSelected) },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                painter = painterResource(Res.drawable.ic_form_check),
                contentDescription = null,
                tint = LightSoftWhite,
                modifier = Modifier.size(16.dp)
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

        LaunchedEffect(Unit) {
            screenModel.initData(id, type)
        }

        ShareAssetContent(
            onBackClick = { navigator.pop() },
            // 🌟 1. ส่งท่อ onPrepareUnshare เชื่อมเข้ากับ ScreenModel
            onPrepareUnshare = { itemToUnshare, onReadyCallback ->
                screenModel.prepareUnshareItem(itemToUnshare, id) { preparedItem ->
                    onReadyCallback(preparedItem)
                }
            },
            onNextClick = { shareTo, unshareList ->
                screenModel.initShareData(shareTo)
                screenModel.submitShare(
                    id = id,
                    type = type,
                    unshareList = unshareList,
                    onSuccess = {
                        navigator.pop()
                    }
                )
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
    // 🌟 2. ประกาศรับ Callback สำหรับการสืบหา shared_item_id
    onPrepareUnshare: (ShareInfo, (ShareInfo) -> Unit) -> Unit = { _, _ -> },
    onNextClick: (ShareTo, List<ShareInfo>) -> Unit = { _, _ -> },
    friendData: List<FriendTargetModel> = emptyList(),
    groupData: List<GroupTargetModel> = emptyList(),
) {
    val selectedFriends = remember { mutableStateListOf<ShareInfo>() }
    val selectedEmails = remember { mutableStateListOf<ShareInfo>() }

    // List เก็บ "คนที่โดนกดลบ (แต่ยังไม่ลบจริง)"
    val itemsToUnshare = remember { mutableStateListOf<ShareInfo>() }

    LaunchedEffect(friendData, groupData) {
        val preSelected = mutableListOf<ShareInfo>()

        friendData.filter { it.isShared }.forEach { friend ->
            preSelected.add(
                ShareInfo(
                    name = friend.friendName,
                    userId = friend.friendId,
                    typeData = "F",
                    subText = friend.email,
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
            if (selectedFriends.none { it.userId == item.userId }) {
                selectedFriends.add(item)
            }
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val openSheet = { showBottomSheet = true }

    var showEmailSheet by remember { mutableStateOf(false) }
    val openInputEmail = { showEmailSheet = true }

    var showEmailInfoTooltip by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp, top = 20.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_back),
                        contentDescription = "Back",
                        tint = LightPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("แชร์ทรัพย์สิน", style = MaterialTheme.typography.titleLarge, color = LightPrimary)
                        Text("คุณต้องการให้ใครเห็นทรัพย์สินนี้ของคุณบ้าง?", style = MaterialTheme.typography.bodyMedium, color = LightPrimary.copy(alpha = 0.7f))
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
                        // โยนก้อนข้อมูลไปทั้ง 2 อัน (ที่แชร์ใหม่ + ที่รอถอนการแชร์)
                        onNextClick(dataToSend, itemsToUnshare.toList())
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
                ) {
                    Text("ยืนยัน", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            SectionHeader(title = "เลือกเพื่อนหรือกลุ่มที่ต้องการแชร์", onAddClick = openSheet)

            Card(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, LightBorder),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (selectedFriends.isEmpty()) {
                        item {
                            Text(
                                "ยังไม่ได้เลือกเพื่อนหรือกลุ่ม",
                                modifier = Modifier.fillMaxWidth().height(200.dp).padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        items(selectedFriends) { friend ->
                            ShareItemWithDelete(
                                data = friend,
                                onDelete = {
                                    // 🌟 3. ถ้าเพื่อนคนนี้เคยกดแชร์ไปแล้ว (isShared == true)
                                    if (friend.isShared == true) {
                                        // เรียกใช้ onPrepareUnshare เพื่อวิ่งไปหา shared_item_id ก่อนโยนลงถังขยะ
                                        onPrepareUnshare(friend) { preparedItem ->
                                            itemsToUnshare.add(preparedItem)
                                        }
                                    }
                                    // ไม่ว่าจะเคยแชร์หรือไม่ ก็ลบออกจากหน้าจอ UI ทันที
                                    selectedFriends.remove(friend)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title = "แชร์ให้คนที่ไม่มีบัญชี",
                showInfo = true,
                onInfoClick = { showEmailInfoTooltip = !showEmailInfoTooltip },
                onAddClick = openInputEmail
            )

            AnimatedVisibility(visible = showEmailInfoTooltip) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, LightBorder, RoundedCornerShape(20.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "หากคนที่คุณต้องการให้เข้าถึงทรัพย์สินนี้\nยังไม่มีบัญชีของ Wealth & Vault\nหรือยังไม่ได้เพิ่มเพื่อน สามารถแชร์ผ่านอีเมลได้",
                        color = LightPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().height(240.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, LightBorder),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (selectedEmails.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp).height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("ยังไม่มีการเพิ่มอีเมล", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    } else {
                        items(selectedEmails) { email ->
                            ShareItemWithDelete(
                                data = email,
                                onDelete = {
                                    if (email.isShared == true) {
                                        // สำหรับ email ถ้ามี API สำหรับลบ email โดยเฉพาะ ค่อยทำเหมือนเพื่อนก็ได้ครับ
                                        itemsToUnshare.add(email)
                                    }
                                    selectedEmails.remove(email)
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = LightBg
            ) {
                FriendSelectionList(
                    alreadySelected = selectedFriends.toList(),
                    friendData = friendData,
                    groupData = groupData,
                    onConfirm = { selectedItems ->
                        val newItems = selectedItems.filter { newItem ->
                            selectedFriends.none { it.userId == newItem.userId }
                        }
                        selectedFriends.addAll(newItems)

                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) showBottomSheet = false
                        }
                    }
                )
            }
        }

        if (showEmailSheet) {
            ModalBottomSheet(
                onDismissRequest = { showEmailSheet = false },
                containerColor = LightBg
            ) {
                AddEmailContent(
                    onConfirm = { email, date, apiDate ->
                        if (email.isNotBlank()) {
                            val isDuplicate = selectedEmails.any { it.userId == email }
                            if (!isDuplicate) {
                                selectedEmails.add(
                                    ShareInfo(
                                        name = email,
                                        userId = email,
                                        date = date,
                                        apiDate = apiDate,
                                        typeData = "E",
                                        subText = email
                                    )
                                )
                            }
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
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = LightPrimary, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            if (showInfo) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp).clickable { onInfoClick() },
                    tint = LightPrimary.copy(alpha = 0.5f)
                )
            }
        }
        IconButton(onClick = onAddClick) {
            Icon(
                painter = painterResource(Res.drawable.ic_common_plus),
                contentDescription = "Add",
                tint = LightPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
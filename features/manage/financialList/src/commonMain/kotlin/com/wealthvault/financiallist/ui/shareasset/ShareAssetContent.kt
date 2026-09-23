package com.wealthvault.financiallist.ui.shareasset

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.generated.resources.ic_form_check
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightText
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareAssetContent(
    onBackClick: () -> Unit = {},
    onPrepareUnshare: (ShareInfo, (ShareInfo) -> Unit) -> Unit = { _, _ -> },
    onNextClick: (ShareTo, List<ShareInfo>) -> Unit = { _, _ -> },
    friendData: List<FriendTargetModel> = emptyList(),
    groupData: List<GroupTargetModel> = emptyList(),
    emailData: List<ShareInfo> = emptyList(),
    isLoading: Boolean = false,
    error: AppError? = null,
    onRetryClick: () -> Unit = {},
) {
    val selectedFriends = remember { mutableStateListOf<ShareInfo>() }
    val selectedEmails = remember { mutableStateListOf<ShareInfo>() }
    val itemsToUnshare = remember { mutableStateListOf<ShareInfo>() }

    LaunchedEffect(friendData, groupData, emailData) {
        val preSelected = mutableListOf<ShareInfo>()

        friendData.filter { it.isShared }.forEach { friend ->
            preSelected.add(
                ShareInfo(
                    name = friend.friendName,
                    userId = friend.friendId,
                    typeData = "F",
                    subText = friend.email, // กันค่า null
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

        emailData.filter { it.isShared == true }.forEach { email ->
            if (selectedEmails.none { it.userId == email.userId }) selectedEmails.add(email)
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
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(46.dp), // 🌟 ปรับสูง 46.dp
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
                ) {
                    if (isLoading) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp),
                        )
                    } else {
                        Text("ยืนยัน", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp)) {
            if (isLoading && friendData.isEmpty() && groupData.isEmpty() && emailData.isEmpty()) {
                ShareLoadingState()
            } else if (error != null && friendData.isEmpty() && groupData.isEmpty() && emailData.isEmpty()) {
                ShareLoadError(error = error, onRetry = onRetryClick)
            } else {
                if (error != null) {
                    ShareInlineError(error = error, onRetry = onRetryClick)
                    Spacer(modifier = Modifier.height(8.dp))
                }
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
                        items(
                            items = selectedFriends,
                            key = { friend ->
                                friend.userId.ifBlank { "${friend.typeData}:${friend.name}" }
                            },
                        ) { friend ->
                            ShareItemWithDelete(data = friend, onDelete = {
                                if (friend.isShared == true) {
                                    onPrepareUnshare(friend) { preparedItem ->
                                        if (itemsToUnshare.none { it.userId == preparedItem.userId && it.typeData == preparedItem.typeData }) {
                                            itemsToUnshare.add(preparedItem)
                                        }
                                        selectedFriends.remove(friend)
                                    }
                                } else {
                                    selectedFriends.remove(friend)
                                }
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
                                items(
                                    items = selectedEmails,
                                    key = { email ->
                                        email.userId.ifBlank { "${email.typeData}:${email.name}" }
                                    },
                                ) { email ->
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

@Composable
private fun ShareLoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.material3.CircularProgressIndicator(color = LightPrimary)
    }
}

@Composable
private fun ShareLoadError(error: AppError, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = shareErrorMessage(error),
                color = LightText,
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
            ) {
                Text("ลองใหม่", color = Color.White)
            }
        }
    }
}

@Composable
private fun ShareInlineError(error: AppError, onRetry: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF0EE), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = shareErrorMessage(error),
            color = LightText,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
        )
        androidx.compose.material3.TextButton(onClick = onRetry) {
            Text("ลองใหม่", color = LightPrimary)
        }
    }
}

private fun shareErrorMessage(error: AppError): String = when (error) {
    AppError.Unauthorized -> "เซสชันหมดอายุ กรุณาเข้าสู่ระบบใหม่"
    AppError.NotFound -> "ไม่พบข้อมูลการแชร์ของทรัพย์สินนี้"
    is AppError.Network -> "เชื่อมต่อเซิร์ฟเวอร์ไม่ได้ กรุณาลองใหม่"
    is AppError.Unknown -> "โหลดข้อมูลการแชร์ไม่สำเร็จ กรุณาลองใหม่"
}

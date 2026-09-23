package com.wealthvault.profile.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.theme.RedErr
import com.wealthvault.core.utils.LocalRootNavigator
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.profile.ui.components.SelectPersonItem
import com.wealthvault.domain.profile.CloseFriendData
import com.wealthvault.domain.profile.FriendData
import com.wealthvault.domain.profile.UserData
import kotlinx.coroutines.delay

class ShareSettingScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ShareSettingScreenModel>()
        val rootNavigator = LocalRootNavigator.current
        // 🌟 ดึงค่า Loading มาจาก ScreenModel
        val isLoading by screenModel.isLoading.collectAsStateWithLifecycle()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(screenModel) {
            screenModel.fetchShareSettingData()
        }

        val userData by screenModel.userState.collectAsStateWithLifecycle()
        val closeFriends by screenModel.closeFriends.collectAsStateWithLifecycle()
        val allFriends by screenModel.allFriends.collectAsStateWithLifecycle()

        ShareSettingContent(
            userData = userData,
            closeFriends = closeFriends,
            allFriends = allFriends,
            isLoading = isLoading, // 🌟 ส่งสถานะลงไปให้ UI วาด
            error = uiState.error,
            onBackClick = { rootNavigator.pop() },
            onRetryClick = { screenModel.fetchShareSettingData() },
            onSettingsChanged = { newEnabled, newAge ->
                screenModel.updateShareSettings(newEnabled, newAge)
            },
            onRemoveFriend = { id ->
                screenModel.removeCloseFriend(id)
            },
            onAddFriends = { ids ->
                screenModel.addCloseFriends(ids)
            },
            onPlusClick = {
                screenModel.fetchAllFriends()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSettingContent(
    userData: UserData?,
    closeFriends: List<CloseFriendData>,
    allFriends: List<FriendData>,
    isLoading: Boolean, // 🌟 รับสถานะ Loading ตรงนี้
    error: AppError?,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onSettingsChanged: (Boolean, Int) -> Unit,
    onRemoveFriend: (String) -> Unit,
    onAddFriends: (List<String>) -> Unit,
    onPlusClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)

    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val selectedFriendIds = remember { mutableStateListOf<String>() }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var friendToDelete by remember { mutableStateOf<CloseFriendData?>(null) }

    var isSharingEnabled by remember { mutableStateOf(false) }
    var sharedAgeText by remember { mutableStateOf("0") }

    LaunchedEffect(userData) {
        if (userData != null) {
            isSharingEnabled = userData.shareEnabled ?: false
            sharedAgeText = userData.sharedAge.toString()
        }
    }

    LaunchedEffect(sharedAgeText) {
        if (userData != null && sharedAgeText != userData.sharedAge.toString()) {
            delay(1000)
            val finalAge = sharedAgeText.toIntOrNull() ?: 0
            onSettingsChanged(isSharingEnabled, finalAge)
        }
    }

    ShareSettingBody(
        userData = userData,
        closeFriends = closeFriends,
        isLoading = isLoading,
        error = error,
        themeColor = themeColor,
        isSharingEnabled = isSharingEnabled,
        sharedAgeText = sharedAgeText,
        onBackClick = onBackClick,
        onRetryClick = onRetryClick,
        onSharingEnabledChange = { newValue ->
            isSharingEnabled = newValue
            val finalAge = sharedAgeText.toIntOrNull() ?: 0
            onSettingsChanged(newValue, finalAge)
        },
        onSharedAgeTextChange = { value ->
            if (value.all { char -> char.isDigit() }) sharedAgeText = value
        },
        onPlusClick = {
            onPlusClick()
            showSheet = true
        },
        onDeleteClick = { friend ->
            friendToDelete = friend
            showDeleteDialog = true
        },
    )

    // --- AlertDialog สำหรับยืนยันการลบ ---
    if (showDeleteDialog && friendToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "ลบคนใกล้ชิด",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = LightText
                )
            },
            text = {
                Text(
                    text = "คุณแน่ใจหรือไม่ว่าต้องการลบ '${friendToDelete?.username}' ออกจากรายชื่อคนใกล้ชิด?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightMuted,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        friendToDelete?.let { onRemoveFriend(it.id) }
                        showDeleteDialog = false
                        friendToDelete = null
                    }
                ) {
                    Text(
                        text = "ลบ",
                        color = RedErr,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(
                        text = "ยกเลิก",
                        color = LightMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        )
    }

    // --- Popup เลือกคนใกล้ชิด (ModalBottomSheet) ---
    if (showSheet) {
        LaunchedEffect(selectedFriendIds.size) {
            if (selectedFriendIds.isNotEmpty()) {
                sheetState.expand()
            }
        }

        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
                selectedFriendIds.clear()
            },
            sheetState = sheetState,
            containerColor = Color(0xFFFDF7F2),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "เลือกคนใกล้ชิด",
                    style = MaterialTheme.typography.titleMedium,
                    color = LightText,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    val availableToAdd = allFriends.filter { friend ->
                        closeFriends.none { it.id == friend.id }
                    }

                    if (availableToAdd.isEmpty()) {
                        item {
                            Text(
                                "ไม่พบรายชื่อเพื่อนที่สามารถเพิ่มได้",
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        items(
                            items = availableToAdd,
                            key = { friend ->
                                friend.id ?: friend.email ?: friend.username ?: friend.hashCode()
                            },
                        ) { friend ->
                            SelectPersonItem(
                                friend = friend,
                                isSelected = selectedFriendIds.contains(friend.id),
                                onSelectedChange = { isSelected ->
                                    if (isSelected) selectedFriendIds.add(friend.id ?: "")
                                    else selectedFriendIds.remove(friend.id)
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        onAddFriends(selectedFriendIds.toList())
                        selectedFriendIds.clear()
                        showSheet = false
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp).height(46.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                    enabled = selectedFriendIds.isNotEmpty()
                ) {
                    Text("เพิ่ม", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

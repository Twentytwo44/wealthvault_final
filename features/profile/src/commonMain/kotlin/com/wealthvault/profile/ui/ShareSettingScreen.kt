package com.wealthvault.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.theme.RedErr
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.profile.ui.components.ClosePersonItem
import com.wealthvault.profile.ui.components.SelectPersonItem
import com.wealthvault.`user-api`.model.CloseFriendData
import com.wealthvault.`user-api`.model.FriendData
import com.wealthvault.`user-api`.model.UserData
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

class ShareSettingScreen(private val onBackClick: () -> Unit) : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ShareSettingScreenModel>()

        LaunchedEffect(Unit) {
            screenModel.fetchUser()
            screenModel.fetchCloseFriends()
        }

        val userData by screenModel.userState.collectAsState()
        val closeFriends by screenModel.closeFriends.collectAsState()
        val allFriends by screenModel.allFriends.collectAsState()

        ShareSettingContent(
            userData = userData,
            closeFriends = closeFriends,
            allFriends = allFriends, // 🌟 ตัวนี้จะเป็น List<FriendData>
            onBackClick = onBackClick,
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
                // 🌟 สั่งโหลดเพื่อนเฉพาะตอนกดปุ่มบวก
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
    onBackClick: () -> Unit,
    onSettingsChanged: (Boolean, Int) -> Unit,
    onRemoveFriend: (String) -> Unit,
    onAddFriends: (List<String>) -> Unit,
    onPlusClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)

    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val selectedFriendIds = remember { mutableStateListOf<String>() }

    // 🌟 1. เพิ่ม State สำหรับ Dialog ยืนยันการลบ
    var showDeleteDialog by remember { mutableStateOf(false) }
    var friendToDelete by remember { mutableStateOf<CloseFriendData?>(null) }

    var isSharingEnabled by remember { mutableStateOf(false) }
    var sharedAgeText by remember { mutableStateOf("0") }

    LaunchedEffect(userData) {
        if (userData != null) {
            isSharingEnabled = userData.shareEnabled
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // --- Header ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_common_back),
                contentDescription = "Back",
                tint = themeColor,
                modifier = Modifier.size(24.dp).clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "ตั้งค่าการแชร์ทรัพย์สิน", style = MaterialTheme.typography.titleLarge, color = themeColor)
        }

        if (userData == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = themeColor)
            }
            return
        }

        // --- Toggle Switch ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "แชร์ทรัพย์สินทั้งหมดให้คนใกล้ชิดตามกำหนด",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF3A2F2A),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isSharingEnabled,
                onCheckedChange = { newValue ->
                    isSharingEnabled = newValue
                    val finalAge = sharedAgeText.toIntOrNull() ?: 0
                    onSettingsChanged(newValue, finalAge)
                },
                thumbContent = { },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = LightSoftWhite,
                    checkedTrackColor = LightPrimary,
                    uncheckedThumbColor = LightSoftWhite,
                    uncheckedTrackColor = Color(0xFFE8DDD7),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }

        // --- Age Setting ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "เปิดให้เห็นทรัพย์สินเมื่อถึงอายุ", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF3A2F2A))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = sharedAgeText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) sharedAgeText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(70.dp).height(50.dp).border(1.dp, LightBorder, RoundedCornerShape(12.dp)),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, color = Color(0xFF3A2F2A)),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = LightSoftWhite,
                        unfocusedContainerColor = LightSoftWhite,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "ปี", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }

        // --- Close People List Header ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "คนใกล้ชิด", style = MaterialTheme.typography.titleMedium, color = Color(0xFF3A2F2A))
            Icon(
                painter = painterResource(Res.drawable.ic_common_plus),
                contentDescription = "Add",
                tint = themeColor,
                modifier = Modifier.size(24.dp)
                    .clickable {
                        onPlusClick()
                        showSheet = true
                    }
            )
        }

        // --- รายการคนใกล้ชิด ---
        LazyColumn(modifier = Modifier.weight(1f)) {
            if (closeFriends.isEmpty()) {
                item {
                    Text(
                        text = "ยังไม่มีคนใกล้ชิด",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    )
                }
            } else {
                items(closeFriends) { friend ->
                    ClosePersonItem(
                        friend = friend,
                        showDelete = true,
                        isEnabled = true,
                        onDeleteClick = {
                            // 🌟 2. เวลากดลบ ให้เก็บค่าคนนั้นไว้แล้วเปิด Dialog แทน
                            friendToDelete = friend
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    // --- 🌟 3. AlertDialog สำหรับยืนยันการลบ ---
    if (showDeleteDialog && friendToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp), // 🌟 1. กำหนดความโค้งให้เข้ากับ Card ในหน้าอื่นๆ
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
                    color = LightMuted, // 🌟 2. ปรับให้เข้มกว่า Color.Gray เพื่อให้อ่านง่ายและดูแพงขึ้น
                    lineHeight = 22.sp // เพิ่มระยะห่างบรรทัดนิดนึงให้อ่านสบายตา
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
                        color = RedErr, // 🌟 3. ใช้สีแดง (Material Red 700) ให้ดูเป็นปุ่มอันตรายชัดเจนขึ้น
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
                        color = LightMuted, // 🌟 4. ใช้สีเทาอมน้ำตาลให้รับกับธีม ไม่ดูเหมือนปุ่มโดน Disable
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
                        items(availableToAdd) { friend ->
                            SelectPersonItem(
                                friend = friend,
                                isSelected = selectedFriendIds.contains(friend.id),
                                onSelectedChange = { isSelected ->
                                    if (isSelected) selectedFriendIds.add(friend.id  ?: "",)
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
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp).height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                    enabled = selectedFriendIds.isNotEmpty()
                ) {
                    Text("เพิ่ม", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

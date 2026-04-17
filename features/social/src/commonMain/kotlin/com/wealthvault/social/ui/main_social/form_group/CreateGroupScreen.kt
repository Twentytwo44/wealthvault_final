package com.wealthvault.social.ui.main_social.form_group

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_pen
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.generated.resources.ic_nav_social
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.theme.RedErr
import com.wealthvault.core.theme.WvWaveGradientEnd
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.social.ui.main_social.components.SelectPersonItem
import com.wealthvault.`user-api`.model.FriendData
import org.jetbrains.compose.resources.painterResource


class CreateGroupScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<CreateGroupScreenModel>()

        val isLoading by screenModel.isLoading.collectAsState()
        val isSuccess by screenModel.isSuccess.collectAsState()

        // รับค่า errorMessage จาก ScreenModel
        val errorMessage by screenModel.errorMessage.collectAsState()

        // 🌟 1. สร้าง State สำหรับคุมการโชว์ Snackbar (แจ้งเตือนข้ามแพลตฟอร์ม)
        val snackbarHostState = remember { SnackbarHostState() }

        val allFriends by screenModel.friends.collectAsState()

        LaunchedEffect(Unit) {
            screenModel.fetchFriends()
        }

        LaunchedEffect(isSuccess) {
            if (isSuccess) {
                navigator.pop()
            }
        }

        // 🌟 2. ถ้ามี Error ให้โชว์ Snackbar
        LaunchedEffect(errorMessage) {
            errorMessage?.let { msg ->
                snackbarHostState.showSnackbar(message = msg)
            }
        }

        // 🌟 3. ใช้ Box ครอบทั้งหมด
        Box(modifier = Modifier.fillMaxSize()) {
            GroupFormContent(
                title = "สร้างกลุ่ม",
                buttonText = "สร้างกลุ่ม",
                availableFriends = allFriends,
                isLoading = isLoading,
                onBackClick = { navigator.pop() },
                onSaveClick = { groupName, selectedMemberIds, imageBytes ->
                    screenModel.createGroup(groupName, selectedMemberIds, imageBytes) // ยิง API สร้าง
                }

            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFC27A5A))
                }
            }

            // 🌟 4. วางตัวแสดง Snackbar ไว้ล่างสุดของจอ
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupFormContent(
    title: String = "สร้างกลุ่ม", // 🌟 รับชื่อหน้าจอ
    buttonText: String = "สร้างกลุ่ม", // 🌟 รับชื่อปุ่ม
    initialGroupName: String = "", // 🌟 ชื่อกลุ่มเดิม (ถ้ามี)
    initialImageUrl: String? = null, // 🌟 รูปกลุ่มเดิม (ถ้ามี)
    initialMemberIds: List<String> = emptyList(), // 🌟 สมาชิกเดิม (ถ้ามี)
    availableFriends: List<FriendData>,
    isLoading: Boolean,
    onBackClick: (Boolean) -> Unit,
    onSaveClick: (String, List<String>, ByteArray?) -> Unit // 🌟 เปลี่ยนชื่อเป็น onSaveClick ให้ดูครอบคลุมทั้งสร้างและแก้
) {
    val themeColor = Color(0xFFC27A5A)
    val scope = rememberCoroutineScope()

    // 🌟 ใส่ค่าเริ่มต้นให้ State
    var groupName by remember { mutableStateOf(initialGroupName) }
    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    val groupMemberIds = remember { mutableStateListOf<String>().apply { addAll(initialMemberIds) } }

    val selectedFriendIds = remember { mutableStateListOf<String>() }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var friendToDelete by remember { mutableStateOf<FriendData?>(null) }

    val hasChanges = groupName != initialGroupName ||
            groupMemberIds.toSet() != initialMemberIds.toSet() ||
            selectedImageBytes != null

    val imagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays -> selectedImageBytes = byteArrays.firstOrNull() }
    )

    Column(
        modifier = Modifier.fillMaxSize().background(LightBg).statusBarsPadding().padding(24.dp)
    ) {
        // --- Header ---
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 32.dp)) {
            Icon(
                painter = painterResource(Res.drawable.ic_common_back),
                contentDescription = "Back", tint = LightPrimary,
                modifier = Modifier.size(24.dp).clickable { onBackClick(hasChanges) }
            )
            Spacer(modifier = Modifier.width(16.dp))
            // 🌟 ใช้ตัวแปร title
            Text(text = title, style = MaterialTheme.typography.titleLarge, color = LightPrimary)
        }

        // --- รูปโปรไฟล์กลุ่ม ---
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.clickable { imagePicker.launch() }) {
                Box(
                    modifier = Modifier.size(120.dp).border(3.dp, LightPrimary, CircleShape).padding(3.dp).clip(CircleShape).background(LightBg),
                    contentAlignment = Alignment.Center
                ) {
                    // 🌟 เช็คว่ามีรูปที่เพิ่งเลือกไหม ถ้าไม่มีให้เช็ครูปเดิมจาก API ถ้าไม่มีอีกค่อยโชว์ไอคอนโล้นๆ
                    if (selectedImageBytes != null) {
                        AsyncImage(model = selectedImageBytes, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    } else if (!initialImageUrl.isNullOrEmpty()) {
                        AsyncImage(model = initialImageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    } else {
                        Icon(painter = painterResource(Res.drawable.ic_nav_social), contentDescription = null, tint = WvWaveGradientEnd, modifier = Modifier.size(50.dp))
                    }
                }
                Box(
                    modifier = Modifier.size(28.dp).offset(x = (-4).dp, y = (-4).dp).clip(CircleShape).background(LightPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(painter = painterResource(Res.drawable.ic_common_pen), contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // --- ใส่ชื่อกลุ่ม ---
        Column {
            Text("ชื่อกลุ่ม", style = MaterialTheme.typography.bodyMedium, color = LightPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = groupName,
                onValueChange = { groupName = it },
                modifier = Modifier.fillMaxWidth().border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightSoftWhite, unfocusedContainerColor = LightSoftWhite,
                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- เลือกสมาชิก ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            Text("สมาชิกในกลุ่ม (${groupMemberIds.size})", style = MaterialTheme.typography.titleMedium, color = Color(0xFF3A2F2A))
            Icon(painter = painterResource(Res.drawable.ic_common_plus), contentDescription = "Add", tint = themeColor, modifier = Modifier.size(24.dp).clickable { showSheet = true })
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            val currentMembers = availableFriends.filter { groupMemberIds.contains(it.id) }
            if (currentMembers.isEmpty()) {
                item { Text("เลือกสมาชิก", color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) }
            } else {
                items(currentMembers) { friend ->
                    GroupMemberItem(friend = friend, onDeleteClick = { friendToDelete = friend; showDeleteDialog = true })
                }
            }
        }

        // --- ปุ่มบันทึก ---
        Button(
            onClick = { onSaveClick(groupName, groupMemberIds.toList(), selectedImageBytes) },
            enabled = groupName.isNotBlank() && groupMemberIds.isNotEmpty() && !isLoading,
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp).height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = themeColor, disabledContainerColor = themeColor.copy(alpha = 0.4f))
        ) {
            // 🌟 ใช้ตัวแปร buttonText
            Text(if (isLoading) "กำลังดำเนินการ..." else buttonText, color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }

    // --- Dialog และ BottomSheet ตามเดิม ---
    if (showDeleteDialog && friendToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("ลบสมาชิก", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = LightText)
            },
            text = {
                Text(
                    text = "คุณต้องการลบ '${friendToDelete?.username ?: friendToDelete?.firstName}' ออกจากกลุ่มใช่หรือไม่?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightMuted,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    friendToDelete?.id?.let { groupMemberIds.remove(it) }
                    showDeleteDialog = false
                    friendToDelete = null
                }) {
                    Text("ลบออก", color = RedErr, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("ยกเลิก", color = LightMuted, fontWeight = FontWeight.Medium)
                }
            }
        )
    }

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
                    text = "เลือกสมาชิกเข้ากลุ่ม",
                    style = MaterialTheme.typography.titleMedium,
                    color = LightText,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    val availableToAdd = availableFriends.filter { friend ->
                        !groupMemberIds.contains(friend.id)
                    }

                    if (availableToAdd.isEmpty()) {
                        item {
                            Text(
                                "ไม่มีรายชื่อเพื่อนที่สามารถเลือกได้",
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
                                    if (isSelected) selectedFriendIds.add(friend.id ?: "")
                                    else selectedFriendIds.remove(friend.id)
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        groupMemberIds.addAll(selectedFriendIds)
                        selectedFriendIds.clear()
                        showSheet = false
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp).height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                    enabled = selectedFriendIds.isNotEmpty()
                ) {
                    Text("เพิ่มเข้ากลุ่ม", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun GroupMemberItem(
    friend: FriendData,
    onDeleteClick: () -> Unit
) {
    val displayName = friend.username ?: friend.firstName ?: "ไม่ระบุชื่อ"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(LightBg),
            contentAlignment = Alignment.Center
        ) {
            if (!friend.profile.isNullOrEmpty()) {
                AsyncImage(
                    model = friend.profile,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    painter = painterResource(Res.drawable.ic_nav_profile),
                    contentDescription = null,
                    tint = LightPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = displayName,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF3A2F2A),
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "ลบ",
            style = MaterialTheme.typography.bodyMedium,
            color = RedErr,
            modifier = Modifier
                .clickable { onDeleteClick() }
                .padding(8.dp)
        )
    }
}




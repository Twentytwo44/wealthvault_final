package com.wealthvault.social.ui.main_social.form_group

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_bin
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
import com.wealthvault.social.ui.main_social.components.SelectPersonItem
import com.wealthvault.`user-api`.model.FriendData
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupFormContent(
    title: String = "สร้างกลุ่ม",
    buttonText: String = "สร้างกลุ่ม",
    initialGroupName: String = "",
    initialImageUrl: String? = null,
    initialMemberIds: List<String> = emptyList(),
    availableFriends: List<FriendData>,
    isLoading: Boolean,
    showDeleteButton: Boolean = false, // 🌟 เพิ่มตัวนี้ (Default false สำหรับตอนสร้างกลุ่ม)
    onDeleteGroupClick: () -> Unit = {}, // 🌟 เพิ่มตัวนี้สำหรับรับ Event ตอนกดถังขยะ
    onBackClick: (Boolean) -> Unit,
    onSaveClick: (String, List<String>, ByteArray?) -> Unit
) {
    val themeColor = Color(0xFFC27A5A)
    val scope = rememberCoroutineScope()

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

    // 🌟 ใช้ Box ครอบทั้งหมดเพื่อให้แสดง Dialog ซ้อนทับเนื้อหาได้เนียนๆ
    Box(modifier = Modifier.fillMaxSize()) {

        // --- ส่วนเนื้อหาหลัก ---
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
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = LightPrimary,
                    modifier = Modifier.weight(1f)
                )

                // 🌟 ถ้า showDeleteButton เป็น true ให้โชว์ถังขยะ
                if (showDeleteButton) {
                    Text(
                        text = "ลบกลุ่ม",
                        color = RedErr,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { onDeleteGroupClick() }
                            .padding(8.dp)
                    )
                }
            }

            // --- รูปโปรไฟล์กลุ่ม ---
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.clickable { imagePicker.launch() }) {
                    Box(
                        modifier = Modifier.size(120.dp).border(3.dp, LightPrimary, CircleShape).padding(3.dp).clip(CircleShape).background(LightBg),
                        contentAlignment = Alignment.Center
                    ) {
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
                        GroupMemberItem(friend = friend, onDeleteClick = {
                            friendToDelete = friend
                            showDeleteDialog = true
                        })
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
                Text(if (isLoading) "กำลังดำเนินการ..." else buttonText, color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        } // จบเนื้อหา Column หลัก

        // --- Dialog ลบเพื่อน (ให้ลอยทับอยู่บนสุด) ---
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

        // --- BottomSheet เลือกเพื่อน (ให้ลอยทับอยู่บนสุด) ---
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
    } // จบ Box
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
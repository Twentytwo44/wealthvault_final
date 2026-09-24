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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.SolidColor
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
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.theme.RedErr
import com.wealthvault.core.theme.WvWaveGradientEnd
import com.wealthvault.social.ui.main_social.components.SelectPersonItem
import com.wealthvault.domain.profile.FriendData
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
    friendsLoading: Boolean = false,
    friendsErrorMessage: String? = null,
    onRetryFriends: () -> Unit = {},
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
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 26.dp)) {
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
                        modifier = Modifier.size(110.dp).border(3.dp, LightPrimary, CircleShape).padding(3.dp).clip(CircleShape).background(LightBg),
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
                BasicTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF3A2F2A)),
                    cursorBrush = SolidColor(LightPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp), // 🌟 ล็อกความสูงให้เท่าหน้าอื่น
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(LightSoftWhite, RoundedCornerShape(12.dp))
                                .border(
                                    width = 1.dp,
                                    color = LightBorder.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                if (groupName.isEmpty()) {
                                    Text(
                                        text = "กรอกชื่อกลุ่ม", // 🌟 ใส่ placeholder ตรงนี้ได้เลยครับ
                                        color = Color.LightGray,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                innerTextField()
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            GroupMembersContent(
                memberIds = groupMemberIds,
                availableFriends = availableFriends,
                friendsLoading = friendsLoading,
                friendsErrorMessage = friendsErrorMessage,
                onRetryFriends = onRetryFriends,
                onAddClick = { showSheet = true },
                onDeleteClick = { friend ->
                    friendToDelete = friend
                    showDeleteDialog = true
                },
                themeColor = themeColor,
                modifier = Modifier.weight(1f)
            )

            // --- ปุ่มบันทึก ---
            Button(
                onClick = { onSaveClick(groupName, groupMemberIds.toList(), selectedImageBytes) },
                enabled = groupName.isNotBlank() && groupMemberIds.isNotEmpty() &&
                    !isLoading && !friendsLoading && friendsErrorMessage == null,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor, disabledContainerColor = themeColor.copy(alpha = 0.4f))
            ) {
                Text(if (isLoading) "กำลังดำเนินการ..." else buttonText, color = Color.White, style = MaterialTheme.typography.bodyLarge)
            }
        } // จบเนื้อหา Column หลัก

        if (showDeleteDialog) {
            GroupDeleteMemberDialog(
                friend = friendToDelete,
                onDismiss = { showDeleteDialog = false },
                onConfirm = { friend ->
                    friend.id?.let { groupMemberIds.remove(it) }
                    showDeleteDialog = false
                    friendToDelete = null
                }
            )
        }

        if (showSheet) {
            GroupFriendPickerSheet(
                availableFriends = availableFriends,
                memberIds = groupMemberIds.toSet(),
                selectedFriendIds = selectedFriendIds.toSet(),
                themeColor = themeColor,
                onSelectedChange = { friendId, isSelected ->
                    if (isSelected) selectedFriendIds.add(friendId)
                    else selectedFriendIds.remove(friendId)
                },
                onDismiss = {
                    showSheet = false
                    selectedFriendIds.clear()
                },
                onConfirm = {
                    groupMemberIds.addAll(selectedFriendIds)
                    selectedFriendIds.clear()
                    showSheet = false
                }
            )
        }
    } // จบ Box
}

internal fun formGroupErrorMessage(error: AppError): String = when (error) {
    AppError.Unauthorized -> "เซสชันหมดอายุ กรุณาเข้าสู่ระบบใหม่"
    AppError.NotFound -> "ไม่พบข้อมูลเพื่อน"
    is AppError.Network -> "เชื่อมต่อเซิร์ฟเวอร์ไม่ได้ กรุณาลองใหม่"
    is AppError.Unknown -> "โหลดรายชื่อเพื่อนไม่สำเร็จ กรุณาลองใหม่"
}

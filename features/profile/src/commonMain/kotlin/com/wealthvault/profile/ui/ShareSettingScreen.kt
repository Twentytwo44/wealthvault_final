package com.wealthvault.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.profile.ui.components.ClosePersonItem
import com.wealthvault.`user-api`.model.CloseFriendData
import com.wealthvault.`user-api`.model.UserData
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.delay

class ShareSettingScreen(private val onBackClick: () -> Unit) : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ProfileScreenModel>()

        LaunchedEffect(Unit) {
            screenModel.fetchUser()
            screenModel.fetchCloseFriends()
        }

        val userData by screenModel.userState.collectAsState()
        val closeFriends by screenModel.closeFriends.collectAsState()

        ShareSettingContent(
            userData = userData,
            closeFriends = closeFriends,
            onBackClick = onBackClick,
            // 🌟 เปลี่ยนชื่อเป็น onSettingsChanged ให้ดูสมเหตุสมผลกับการ Auto-save
            onSettingsChanged = { newShareEnabled, newSharedAge ->
                // 🌟 TODO: เรียก API อัปเดตข้อมูลตรงนี้เลยครับ!
                // screenModel.updateShareSettings(newShareEnabled, newSharedAge)
                println("🚀 Auto-Saving: Enabled=$newShareEnabled, Age=$newSharedAge")
            }
        )
    }
}

@Composable
fun ShareSettingContent(
    userData: UserData?,
    closeFriends: List<CloseFriendData>,
    onBackClick: () -> Unit,
    onSettingsChanged: (Boolean, Int) -> Unit // 🌟 เปลี่ยนชื่อพารามิเตอร์รับค่า
) {
    val themeColor = Color(0xFFC27A5A)

    var isSharingEnabled by remember { mutableStateOf(false) }
    var sharedAgeText by remember { mutableStateOf("0") }

    LaunchedEffect(userData) {
        if (userData != null) {
            isSharingEnabled = userData.shareEnabled
            sharedAgeText = userData.sharedAge.toString()
        }
    }

    // 🌟 ใช้ LaunchedEffect แบบหน่วงเวลา (Debounce) สำหรับช่องกรอกอายุ
    // เพื่อไม่ให้มันยิง API รัวๆ ทุกครั้งที่พิมพ์ตัวเลขแต่ละตัว
    LaunchedEffect(sharedAgeText) {
        if (userData != null && sharedAgeText != userData.sharedAge.toString()) {
            delay(1000) // รอให้ผู้ใช้พิมพ์เสร็จสัก 1 วินาทีค่อยเซฟ
            val finalAge = sharedAgeText.toIntOrNull() ?: 0
            onSettingsChanged(isSharingEnabled, finalAge)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "ตั้งค่าการแชร์ทรัพย์สิน",
                style = MaterialTheme.typography.titleLarge,
                color = themeColor
            )
        }

        if (userData == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = themeColor)
            }
            return
        }

        // --- Toggle Switch (เปิด/ปิด แชร์) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
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
                // 🌟 ทันทีที่กด Switch ให้ยิง API ไปเซฟเลยครับ
                onCheckedChange = { newValue ->
                    isSharingEnabled = newValue
                    val finalAge = sharedAgeText.toIntOrNull() ?: 0
                    onSettingsChanged(newValue, finalAge)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = themeColor,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }

        // --- Age Setting (ช่องกรอกอายุ) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "เปิดให้เห็นทรัพย์สินเมื่อถึงอายุ",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF3A2F2A)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = sharedAgeText,
                    onValueChange = { newText ->
                        if (newText.all { it.isDigit() }) {
                            sharedAgeText = newText
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(70.dp)
                        .height(50.dp)
                        .border(
                            width = 1.dp,
                            color = LightBorder,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = Color(0xFF3A2F2A)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ปี",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        // --- Close People List Header ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "คนใกล้ชิด",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF3A2F2A)
            )
            Icon(
                painter = painterResource(Res.drawable.ic_common_plus),
                contentDescription = "Add",
                tint = themeColor,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* TODO: เปิด Popup เพิ่มคน */ }
            )
        }

        // --- รายการคนใกล้ชิด ---
        LazyColumn(modifier = Modifier.weight(1f)) {
            if (closeFriends.isEmpty()) {
                item {
                    Text(
                        text = "ยังไม่มีข้อมูลคนใกล้ชิด",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                items(closeFriends) { friend ->
                    ClosePersonItem(
                        friend = friend,
                        showDelete = true,
                        isEnabled = true,
                        onDeleteClick = {
                            /* TODO: สั่งลบเพื่อน */
                        }
                    )
                }
            }
        }

    }
}
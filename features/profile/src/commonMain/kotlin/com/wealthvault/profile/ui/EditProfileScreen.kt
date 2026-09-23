package com.wealthvault.profile.ui

// 🌟 Import สีจาก Theme ของคุณ Champ

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import coil3.compose.AsyncImage
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_calendar
import com.wealthvault.core.generated.resources.ic_common_pen
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.WvWaveGradientEnd
import com.wealthvault.core.utils.LocalRootNavigator
import com.wealthvault.core.utils.formatThaiDate
import com.wealthvault.core.utils.getScreenModel
import org.jetbrains.compose.resources.painterResource

class EditProfileScreen() : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<EditProfileScreenModel>()
        val rootNavigator = LocalRootNavigator.current

        LaunchedEffect(Unit) {
            screenModel.resetSaveState()
            screenModel.fetchUser()
        }

        EditProfileContent(
            screenModel = screenModel,
            onBackClick = {
                rootNavigator.pop()
            },
            onSaveClick = {
                rootNavigator.pop()
                rootNavigator.pop()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    screenModel: EditProfileScreenModel,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val userData by screenModel.userState.collectAsStateWithLifecycle()
    val uiState by screenModel.uiState.collectAsStateWithLifecycle()
    val isLoading by screenModel.isLoading.collectAsStateWithLifecycle()
    val isSaving by screenModel.isSaving.collectAsStateWithLifecycle()
    val saveSuccess by screenModel.saveSuccess.collectAsStateWithLifecycle()

    val profileImageByteArray by screenModel.profileImageByteArray.collectAsStateWithLifecycle()

    if (userData == null && isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = LightPrimary)
        }
        return
    }

    if (userData == null && uiState.error != null) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("ไม่สามารถโหลดข้อมูลโปรไฟล์ได้", color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { screenModel.fetchUser() },
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
            ) {
                Text("ลองใหม่", color = Color.White)
            }
        }
        return
    }

    var username by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var apiBirthDate by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val imagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            val selectedImageBytes = byteArrays.firstOrNull()
            if (selectedImageBytes != null) {
                screenModel.setProfileImageByteArray(selectedImageBytes)
            }
        }
    )

    LaunchedEffect(userData) {
        userData?.let { user ->
            username = user.username ?: ""
            firstName = user.firstName ?: ""
            lastName = user.lastName ?: ""

            val rawApiDate = user.birthday?.take(10) ?: ""
            apiBirthDate = rawApiDate
            birthDate = formatThaiDate(rawApiDate)

            phone = user.phoneNumber ?: ""
            screenModel.setProfileImageByteArray(null)
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            screenModel.resetSaveState()
            onSaveClick()
        }
    }

    // 🌟 1. Outer Column รองรับการดึงหน้าจอขึ้นหนีคีย์บอร์ด
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .statusBarsPadding()
            .imePadding() // 🌟 สำคัญมาก! ช่วยให้คีย์บอร์ดไม่บังปุ่ม
    ) {

        // 🌟 2. Inner Column สำหรับส่วนที่เลื่อนได้ (ฟอร์มกรอกข้อมูล)
        Column(
            modifier = Modifier
                .weight(1f) // กินพื้นที่ตรงกลางทั้งหมด
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()) // 🌟 ทำให้เลื่อนขึ้นลงได้
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 26.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_common_back),
                    contentDescription = "Back",
                    tint = LightPrimary,
                    modifier = Modifier.size(24.dp).clickable { onBackClick() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "แก้ไขโปรไฟล์", style = MaterialTheme.typography.titleLarge, color = LightPrimary)
            }

            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .border(width = 3.dp, color = LightPrimary, shape = CircleShape)
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(LightBg),
                    contentAlignment = Alignment.Center
                ) {
                    val displayData: Any? = profileImageByteArray ?: userData?.profile

                    val hasValidImage = displayData != null && (displayData !is String || displayData.isNotEmpty())

                    if (hasValidImage) {
                        AsyncImage(
                            model = displayData,
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            painter = painterResource(Res.drawable.ic_nav_profile),
                            contentDescription = "Default Profile",
                            tint = WvWaveGradientEnd,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .clip(CircleShape)
                        .background(LightPrimary)
                        .clickable { imagePicker.launch() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_pen),
                        contentDescription = "Edit Picture",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = userData?.email ?: "กำลังโหลด...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(26.dp))

            ProfileTextField(label = "ชื่อผู้ใช้", value = username, onValueChange = { username = it })
            Spacer(modifier = Modifier.height(14.dp))
            ProfileTextField(label = "ชื่อจริง", value = firstName, onValueChange = { firstName = it })
            Spacer(modifier = Modifier.height(14.dp))
            ProfileTextField(label = "นามสกุล", value = lastName, onValueChange = { lastName = it })
            Spacer(modifier = Modifier.height(14.dp))

            ProfileTextField(
                label = "วันเกิด",
                value = birthDate,
                onValueChange = { },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_calendar),
                        contentDescription = "Calendar",
                        tint = LightPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { showDatePicker = true }
                    )
                }
            )
            Spacer(modifier = Modifier.height(14.dp))
            ProfileTextField(label = "เบอร์โทร", value = phone, onValueChange = { phone = it })

            // 🌟 เพิ่ม Spacer ล่างสุดเพื่อให้เลื่อนลงมาแล้วมีระยะเว้นสวยๆ ไม่ชิดขอบจอเกินไป
            Spacer(modifier = Modifier.height(32.dp))
        }

        // 🌟 3. ปุ่มบันทึก (ติดขอบล่างเสมอ)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Button(
                onClick = {
                    screenModel.saveProfile(username, firstName, lastName, apiBirthDate, phone)
                },
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("บันทึก", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }

    if (showDatePicker) {
        EditProfileDatePicker(
            onDismiss = { showDatePicker = false },
            onDateSelected = { selectedDate ->
                apiBirthDate = selectedDate
                birthDate = formatThaiDate(selectedDate)
            },
        )
    }
}

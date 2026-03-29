package com.wealthvault.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush // 🌟 นำเข้า Brush สำหรับ Gradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_calendar
import com.wealthvault.core.generated.resources.ic_common_pen
import com.wealthvault.core.utils.getScreenModel
import org.jetbrains.compose.resources.painterResource

import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder

// 🌟 Import สีจาก Theme ของคุณ Champ
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.WvBgGradientStart
import com.wealthvault.core.theme.WvBgGradientEnd
import com.wealthvault.core.theme.WvWaveGradientEnd

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class EditProfileScreen(
    private val onBackClick: () -> Unit,
    private val onSaveClick: () -> Unit
) : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<EditProfileScreenModel>()

        LaunchedEffect(Unit) {
            screenModel.resetSaveState()
            screenModel.fetchUser()
        }

        EditProfileContent(
            screenModel = screenModel,
            onBackClick = onBackClick,
            onSaveClick = onSaveClick
        )
    }
}

fun formatToDisplayDate(apiDate: String): String {
    if (apiDate.isBlank()) return ""
    val parts = apiDate.split("-")
    if (parts.size == 3) {
        return "${parts[2]}/${parts[1]}/${parts[0]}"
    }
    return apiDate
}

fun formatToApiDate(displayDate: String): String {
    if (displayDate.isBlank()) return ""
    val parts = displayDate.split("/")
    if (parts.size == 3) {
        return "${parts[2]}-${parts[1]}-${parts[0]}"
    }
    return displayDate
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    screenModel: EditProfileScreenModel,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val userData by screenModel.userState.collectAsState()
    val isSaving by screenModel.isSaving.collectAsState()
    val saveSuccess by screenModel.saveSuccess.collectAsState()

    val profileImageByteArray by screenModel.profileImageByteArray.collectAsState()

    var username by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

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
            username = user.username
            firstName = user.firstName
            lastName = user.lastName
            val rawApiDate = user.birthday.take(10)
            birthDate = formatToDisplayDate(rawApiDate)
            phone = user.phoneNumber
            screenModel.setProfileImageByteArray(null)
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            screenModel.resetSaveState()
            onSaveClick()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            // 🌟 1. ใส่สีพื้นหลังแบบ Gradient ตาม Theme
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(WvBgGradientStart, WvBgGradientEnd)
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_common_back),
                contentDescription = "Back",
                tint = LightPrimary, // 🌟 ใช้ LightPrimary
                modifier = Modifier.size(24.dp).clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "แก้ไขโปรไฟล์", style = MaterialTheme.typography.titleLarge, color = LightPrimary) // 🌟 ใช้ LightPrimary
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .border(width = 3.dp, color = LightPrimary, shape = CircleShape) // 🌟 ใช้ LightPrimary
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
                        // 🌟 โชว์ไอคอนนี้ถ้า User ยังไม่มีรูปโปรไฟล์ หรือ API ส่ง String ว่างมา
                        Icon(
                            painter = painterResource(Res.drawable.ic_nav_profile),
                            contentDescription = "Default Profile",
                            tint = WvWaveGradientEnd, // ให้สีกลืนไปกับขอบ
                            modifier = Modifier.size(50.dp) // ปรับขนาดไอคอนให้อยู่ตรงกลางสวยๆ
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .clip(CircleShape)
                        .background(LightPrimary) // 🌟 ใช้ LightPrimary
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
        }

        Spacer(modifier = Modifier.height(32.dp))

        ProfileTextField(label = "ชื่อผู้ใช้", value = username, onValueChange = { username = it })
        Spacer(modifier = Modifier.height(16.dp))
        ProfileTextField(label = "ชื่อจริง", value = firstName, onValueChange = { firstName = it })
        Spacer(modifier = Modifier.height(16.dp))
        ProfileTextField(label = "นามสกุล", value = lastName, onValueChange = { lastName = it })
        Spacer(modifier = Modifier.height(16.dp))

        ProfileTextField(
            label = "วันเกิด",
            value = birthDate,
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_common_calendar),
                    contentDescription = "Calendar",
                    tint = LightPrimary, // 🌟 ใช้ LightPrimary
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { showDatePicker = true }
                )
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        ProfileTextField(label = "เบอร์โทร", value = phone, onValueChange = { phone = it })

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val apiReadyBirthDate = formatToApiDate(birthDate)
                screenModel.saveProfile(username, firstName, lastName, apiReadyBirthDate, phone)
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LightPrimary), // 🌟 ใช้ LightPrimary
            enabled = !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text("บันทึก", color = Color.White, style = MaterialTheme.typography.titleMedium)
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = Instant.fromEpochMilliseconds(millis)
                        val localDate = instant.toLocalDateTime(TimeZone.UTC)

                        val day = localDate.dayOfMonth.toString().padStart(2, '0')
                        val month = localDate.monthNumber.toString().padStart(2, '0')
                        val year = localDate.year.toString()

                        birthDate = "$day/$month/$year"
                    }
                    showDatePicker = false
                }) {
                    Text("ตกลง", color = LightPrimary) // 🌟 ใช้ LightPrimary
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("ยกเลิก", color = Color.Gray)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = LightPrimary, // 🌟 ใช้ LightPrimary
                    todayDateBorderColor = LightPrimary, // 🌟 ใช้ LightPrimary
                    todayContentColor = LightPrimary // 🌟 ใช้ LightPrimary
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = LightPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                // 🌟 เพิ่มกรอบ (Border) ตรงนี้
                .border(
                    width = 1.dp, // กำหนดความหนาของเส้นขอบ (ปรับได้ตามต้องการ)
                    color = LightBorder, // สีจาก Theme
                    shape = RoundedCornerShape(12.dp) // ต้องกำหนด shape ให้ตรงกับ TextField ด้วย
                ),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            trailingIcon = trailingIcon
        )
    }
}
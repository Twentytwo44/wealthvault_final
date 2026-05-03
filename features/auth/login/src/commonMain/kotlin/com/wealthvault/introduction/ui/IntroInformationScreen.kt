package com.wealthvault.introduction.ui // ถ้าอยากย้าย package ไปโฟลเดอร์ ic_nav_profile ก็เปลี่ยนตรงนี้ได้นะครับ

// Import Theme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_calendar
import com.wealthvault.core.generated.resources.ic_common_pen
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.utils.formatThaiDate
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.navigation.MainScreen
import kotlinx.datetime.Instant // ✅ ต้องใช้ตัวนี้
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource


class IntroQuestionScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        // ดึง ScreenModel ผ่าน Koin (หรือกระบวนการ DI ของคุณ)
        val screenModel = getScreenModel<IntroScreenModel>()

        IntroQuestionContent(
            firstName = screenModel.firstName,
            onFirstNameChange = { screenModel.firstName = it },
            lastName = screenModel.lastName,
            onLastNameChange = { screenModel.lastName = it },
            phoneNum = screenModel.phoneNum,
            onPhoneNumChange = { screenModel.phoneNum = it },
            dob = screenModel.birthday,
            picture = screenModel.picture,
            onDobChange = { screenModel.birthday = it },
            onBackClick = { navigator.pop() },
            onNextClick = {
                screenModel.updateProfile {
                    // จะรันเมื่อ API สำเร็จเท่านั้น
                    navigator.replaceAll(MainScreen())
                }
            },
            screenModel = screenModel
        )

        // แสดง Error Dialog ถ้ามี
        if (screenModel.errorMessage != null) {
            // TODO: แสดง AlertDialog หรือ Snackbar เพื่อแจ้งเตือน
        }

        // แสดง Loading Overlay
        if (screenModel.isLoading) {
            // TODO: แสดง Box ทับหน้าจอพร้อม CircularProgressIndicator
        }
    }
}
@Composable
fun IntroQuestionContent(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    phoneNum: String,
    onPhoneNumChange: (String) -> Unit,
    dob: String, // วันเกิด,
    onDobChange: (String) -> Unit,
    picture: ByteArray?,
    onBackClick: () -> Unit, // ปุ่มย้อนกลับ
    onNextClick: () -> Unit,
    screenModel: IntroScreenModel
) {

    val scope = rememberCoroutineScope()
    val imagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                screenModel.picture = it // บันทึกรูปลง Model ทันที
            }
        }
    )
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var apiBirthDate by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }





    WavyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp)
        ) {
            // --- แถบบนสุด: ปุ่มย้อนกลับ ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TODO: ใส่ Icon ย้อนกลับตรงนี้
                Text(
                    text = "< ย้อนกลับ",
                    color = LightPrimary,
                    modifier = Modifier.clickable { onBackClick() }
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- ส่วนเนื้อหาฟอร์ม (ใส่ Scroll เผื่อคีย์บอร์ดบังจอ) ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()), // 💡 เพิ่ม Scroll ป้องกันคีย์บอร์ดบังปุ่ม
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ข้อมูลส่วนตัว",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    color = LightPrimary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // รูปโปรไฟล์
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .border(3.dp, LightPrimary, CircleShape)
                                .padding(3.dp)
                                .clip(CircleShape)
                                .background(LightSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            if (picture != null) {
                                // แสดงรูปที่เลือกจาก ByteArray
                                AsyncImage(
                                    model = picture,
                                    contentDescription = "Profile",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                // ไอคอน Default
                                Icon(
                                    painter = painterResource(Res.drawable.ic_nav_profile),
                                    contentDescription = "Default",
                                    tint = LightPrimary.copy(alpha = 0.5f),
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                        // ปุ่มดินสอ
                        IconButton(
                            onClick = {imagePicker.launch()},
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(LightPrimary)
                        ) {
                            Icon(painterResource(Res.drawable.ic_common_pen), null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // ช่องกรอกข้อมูลต่างๆ
                InputField(label = "ชื่อจริง", value = firstName, onValueChange = onFirstNameChange, primaryColor = LightPrimary, inputBgColor = LightSurface, borderColor = LightBorder)
                Spacer(modifier = Modifier.height(16.dp))

                InputField(label = "นามสกุล", value = lastName, onValueChange = onLastNameChange, primaryColor = LightPrimary, inputBgColor = LightSurface, borderColor = LightBorder)
                Spacer(modifier = Modifier.height(16.dp))

                IntroTextField(
                    label = "วันเกิด",
                    value = birthday,
                    onValueChange = onDobChange,
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

                InputField(label = "เบอร์โทร", value = phoneNum, onValueChange = onPhoneNumChange, primaryColor = LightPrimary, inputBgColor = LightSurface, borderColor = LightBorder)

                Spacer(modifier = Modifier.height(32.dp))
            }

            // --- ปุ่ม ต่อไป (ยึดติดขอบล่างเสมอ) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            ) {
                Button(
                    onClick = onNextClick,
                    modifier = Modifier
                        .align(Alignment.End)
                        .width(140.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(percent = 30),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
                ) {
                    Text("ต่อไป", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            // ... ภายใน DatePickerDialog ...
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // ✅ ใช้ Instant จาก kotlinx.datetime
                        val instant = Instant.fromEpochMilliseconds(millis)
                        val localDate = instant.toLocalDateTime(TimeZone.UTC).date // ดึงเฉพาะส่วนวันที่

                        val day = localDate.dayOfMonth.toString().padStart(2, '0')
                        val month = localDate.monthNumber.toString().padStart(2, '0')
                        val engYear = localDate.year.toString()

                        // 🌟 1. ค่าที่จะส่งเข้า API (YYYY-MM-DD)
                        val isoDate = "$engYear-$month-$day"

                        // 🌟 2. อัปเดตค่ากลับไปที่ ScreenModel (สำคัญมาก!)
                        onDobChange(isoDate)

                        // 🌟 3. อัปเดต UI ภาษาไทย (ใช้แสดงผลในช่อง TextField เท่านั้น)
                        birthday = formatThaiDate(isoDate)
                    }
                    showDatePicker = false
                }) {
                    Text("ตกลง", color = LightPrimary)
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

// Widget ช่วยสร้างช่องกรอกข้อความ
@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit, primaryColor: Color, inputBgColor: Color, borderColor: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = primaryColor, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(percent = 30),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = inputBgColor, unfocusedContainerColor = inputBgColor,
                focusedBorderColor = primaryColor, unfocusedBorderColor = borderColor,
            )
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroTextField(
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
                .border(
                    width = 1.dp,
                    color = LightBorder.copy(alpha = 0.5f), // ปรับให้จางลงนิดนึงจะสวยมากครับ
                    shape = RoundedCornerShape(12.dp)
                ),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                // 🌟 เปลี่ยนสีพื้นหลังช่อง Input เป็น LightSoftWhite
                focusedContainerColor = LightSoftWhite,
                unfocusedContainerColor = LightSoftWhite,

                // ปิดเส้นขีดด้านล่าง (Indicator) เพราะเราใช้ Border แทนแล้ว
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,

                // ปรับสีตัวหนังสือให้เข้ากับธีม
                focusedTextColor = Color(0xFF3A2F2A),
                unfocusedTextColor = Color(0xFF3A2F2A)
            ),
            trailingIcon = trailingIcon,
            singleLine = true,
        )
    }
}

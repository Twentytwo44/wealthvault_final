package com.wealthvault.introduction.ui

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
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
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource

class IntroQuestionScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
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
                    navigator.replaceAll(MainScreen())
                }
            },
            screenModel = screenModel
        )

        if (screenModel.errorMessage != null) {
            // TODO: แสดง AlertDialog
        }

        if (screenModel.isLoading) {
            // TODO: แสดง Loading Overlay
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroQuestionContent(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    phoneNum: String,
    onPhoneNumChange: (String) -> Unit,
    dob: String,
    onDobChange: (String) -> Unit,
    picture: ByteArray?,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    screenModel: IntroScreenModel
) {
    val scope = rememberCoroutineScope()
    val imagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                screenModel.picture = it
            }
        }
    )
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var birthday by remember { mutableStateOf("") }

    // 🌟 ดักการกรอกข้อมูล: ตรวจสอบว่ากรอกครบทุกช่องหรือยัง
    val isFormValid = firstName.isNotBlank() && lastName.isNotBlank() && dob.isNotBlank() && phoneNum.isNotBlank()

    WavyBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp, bottom = 24.dp)
                .statusBarsPadding()
                .imePadding() // 🌟 สำคัญมาก! ช่วยดัน UI ขึ้นเวลาคีย์บอร์ดเด้ง ไม่ให้บังช่องกรอกหรือปุ่ม
        ) {
            // --- ส่วนเนื้อหาฟอร์ม (เลื่อนได้) ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()) // 🌟 ย้าย Scroll มาอยู่ก่อน Padding เพื่อให้เลื่อนได้สุดขอบจอ
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ส่วนหัว: สไตล์เหมือน EditProfile
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
                    Text(
                        text = "ข้อมูลส่วนตัว",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightPrimary
                    )
                }

                // รูปโปรไฟล์
                Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.clickable { imagePicker.launch() }) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .border(width = 3.dp, color = LightPrimary, shape = CircleShape)
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(LightSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        if (picture != null) {
                            AsyncImage(
                                model = picture,
                                contentDescription = "Profile",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                painter = painterResource(Res.drawable.ic_nav_profile),
                                contentDescription = "Default",
                                tint = LightPrimary.copy(alpha = 0.5f),
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .clip(CircleShape)
                            .background(LightPrimary),
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

                Spacer(modifier = Modifier.height(30.dp))

                // ฟิลด์บังคับกรอก (มีดอกจัน *)
                ProfileTextField(label = "ชื่อจริง*", value = firstName, onValueChange = onFirstNameChange, placeholder = "กรอกชื่อจริง")
                Spacer(modifier = Modifier.height(14.dp))

                ProfileTextField(label = "นามสกุล*", value = lastName, onValueChange = onLastNameChange, placeholder = "กรอกนามสกุล")
                Spacer(modifier = Modifier.height(14.dp))

                ProfileTextField(
                    label = "วันเกิด*",
                    value = birthday,
                    onValueChange = onDobChange,
                    readOnly = true,
                    placeholder = "เลือกวันเกิด",
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

                ProfileTextField(label = "เบอร์โทร*", value = phoneNum, onValueChange = onPhoneNumChange, placeholder = "กรอกเบอร์โทร")

                // เพิ่มระยะห่างด้านล่างเวลาเลื่อนสุดจอ
                Spacer(modifier = Modifier.height(32.dp))
            }

            // --- ปุ่ม ต่อไป (ติดขอบล่างเสมอ) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp), // เพิ่มระยะห่างจากฟอร์มด้านบนนิดหน่อย
            ) {
                Button(
                    onClick = onNextClick,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightPrimary,
                        disabledContainerColor = LightBorder
                    ),
                    enabled = isFormValid
                ) {
                    Text(
                        text = "ต่อไป",
                        color = if (isFormValid) Color.White else Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
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
                        val localDate = instant.toLocalDateTime(TimeZone.UTC).date

                        val day = localDate.dayOfMonth.toString().padStart(2, '0')
                        val month = localDate.monthNumber.toString().padStart(2, '0')
                        val engYear = localDate.year.toString()

                        val isoDate = "$engYear-$month-$day"
                        onDobChange(isoDate)
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
                    selectedDayContainerColor = LightPrimary,
                    todayDateBorderColor = LightPrimary,
                    todayContentColor = LightPrimary
                )
            )
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    placeholder: String = "",
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = LightPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = readOnly,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF3A2F2A)),
            cursorBrush = SolidColor(LightPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
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
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        innerTextField()
                    }

                    if (trailingIcon != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        trailingIcon()
                    }
                }
            }
        )
    }
}
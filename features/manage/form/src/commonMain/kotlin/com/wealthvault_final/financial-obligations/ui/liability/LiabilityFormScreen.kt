package com.wealthvault_final.`financial-obligations`.ui.liability

// 🌟 Import Theme ของแอป

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_calendar
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault_final.`financial-asset`.Imagepicker.Attachment
import com.wealthvault_final.`financial-asset`.Imagepicker.rememberFilePicker
import com.wealthvault_final.`financial-asset`.ui.components.AssetTextField
import com.wealthvault_final.`financial-asset`.ui.components.ReferenceImagepicker
import com.wealthvault_final.`financial-asset`.ui.components.maptype.DropdownInput
import com.wealthvault_final.`financial-asset`.ui.components.maptype.liabilityTypes
import com.wealthvault_final.`financial-asset`.ui.share.ShareAssetScreen
import com.wealthvault_final.`financial-obligations`.model.LiabilityModel
import com.wealthvault_final.`financial-obligations`.ui.liability.viewmodel.LiabilityScreenModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource

class LiabilityFormScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { LiabilityScreenModel() }

        LiabilityInputForm(
            onBackClick = { navigator.pop() },
            onNextClick = { data ->
                screenModel.updateForm(data)
                navigator.push(ShareAssetScreen(request = data))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiabilityInputForm(
    onBackClick: () -> Unit = {},
    onNextClick: (LiabilityModel) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }

    // ยอดเงิน และ ดอกเบี้ย
    var principal by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var creditor by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // 🌟 จัดการ State ปฏิทิน วันที่เริ่มต้น
    var statedAt by remember { mutableStateOf("") } // โชว์บน UI (พ.ศ.)
    var apiStartedAt by remember { mutableStateOf("") } // ส่ง Backend (YYYY-MM-DD)
    var showStartDatePicker by remember { mutableStateOf(false) }
    val startDatePickerState = rememberDatePickerState()

    // 🌟 จัดการ State ปฏิทิน วันที่สิ้นสุด
    var endedAt by remember { mutableStateOf("") } // โชว์บน UI (พ.ศ.)
    var apiEndedAt by remember { mutableStateOf("") } // ส่ง Backend (YYYY-MM-DD)
    var showEndDatePicker by remember { mutableStateOf(false) }
    val endDatePickerState = rememberDatePickerState()

    val attachments = remember { mutableStateListOf<Attachment>() }
    val filePicker = rememberFilePicker { newFiles -> attachments.addAll(newFiles) }

    // 🌟 เช็คข้อมูลจำเป็น (ต้องกรอกครบถึงจะไปต่อได้)
    val isFormValid = name.isNotBlank() && type.isNotBlank() && principal.isNotBlank() && interestRate.isNotBlank() && apiStartedAt.isNotBlank()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                    title = {
                        Text("ข้อมูลหนี้สิน", color = LightPrimary, style = MaterialTheme.typography.titleLarge)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_common_back),
                                contentDescription = "Back",
                                tint = LightPrimary,
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = {
                        val data = LiabilityModel(
                            name = name,
                            type = type,
                            principal = principal.toDoubleOrNull() ?: 0.0,
                            interestRate = interestRate, // โยนแค่ตัวเลขไป
                            startedAt = apiStartedAt, // 🌟 ส่งตัว YYYY-MM-DD ให้ Backend
                            endedAt = apiEndedAt,     // 🌟 ส่งตัว YYYY-MM-DD ให้ Backend
                            creditor = creditor,
                            description = description,
                            attachments = attachments.toList()
                        )
                        onNextClick(data)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid // 🌟 เปิดปุ่มเฉพาะตอนกรอกครบ
                ) {
                    Text("ต่อไป", style = MaterialTheme.typography.titleMedium, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            DropdownInput(
                label = "ประเภทหนี้สิน/ค่าใช้จ่าย*",
                options = liabilityTypes,
                selectedValue = type,
                onValueChange = { type = it }
            )

            AssetTextField(value = name, onValueChange = { name = it }, label = "ชื่อรายการ*", placeholder = "กรอกชื่อรายการ")

            // 🌟 Custom TextField สำหรับดอกเบี้ย (มี % ต่อท้าย + กรอกได้แค่ทศนิยม)
            CustomTextField(
                value = interestRate,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) interestRate = it },
                label = "ดอกเบี้ย*",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = { Text("%", color = LightPrimary, modifier = Modifier.padding(end = 16.dp)) }
            )

            // 🌟 ยอดเงินต้น (กรอกได้แค่ทศนิยม)
            CustomTextField(
                value = principal,
                onValueChange = { if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*\$"))) principal = it },
                label = "จำนวน*",
                placeholder = "0.00",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // 🌟 วันที่เริ่มต้น
            CustomTextField(
                value = statedAt,
                onValueChange = { },
                label = "วันที่เริ่มต้น*",
                placeholder = "วว/ดด/ปปปป",
                readOnly = true,
                trailingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_calendar),
                        contentDescription = "Calendar",
                        tint = LightPrimary,
                        modifier = Modifier
                            .size(24.dp)
                    )
                },
                onClick = { showStartDatePicker = true } // เปิดปฏิทินเมื่อกด
            )

            // 🌟 วันที่สิ้นสุด
            CustomTextField(
                value = endedAt,
                onValueChange = { },
                label = "วันที่สิ้นสุด",
                placeholder = "วว/ดด/ปปปป",
                readOnly = true,
                trailingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_calendar),
                        contentDescription = "Calendar",
                        tint = LightPrimary,
                        modifier = Modifier
                            .size(24.dp)
                    )
                },
                onClick = { showEndDatePicker = true } // เปิดปฏิทินเมื่อกด
            )

            AssetTextField(value = creditor, onValueChange = { creditor = it }, label = "ผู้รับผิดชอบ", placeholder = "กรอกผู้รับผิดชอบ")
            AssetTextField(value = description, onValueChange = { description = it }, label = "คำอธิบาย", placeholder = "รายละเอียดเพิ่มเติม", isMultiLine = true)

            Spacer(modifier = Modifier.height(24.dp))

            ReferenceImagepicker(
                attachments = attachments,
                onAddImage = { filePicker.launchImage() },
                onAddPdf = { filePicker.launchPdf() },
                onRemove = { item -> attachments.remove(item) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // =====================================
        // 📅 DatePicker วันที่เริ่มต้น
        // =====================================
        if (showStartDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showStartDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        startDatePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.fromEpochMilliseconds(millis)
                            val localDate = instant.toLocalDateTime(TimeZone.UTC)

                            val day = localDate.dayOfMonth.toString().padStart(2, '0')
                            val month = localDate.monthNumber.toString().padStart(2, '0')
                            val engYear = localDate.year.toString()

                            apiStartedAt = "$engYear-$month-$day" // สำหรับส่ง Backend
                            statedAt = "$day/$month/${localDate.year + 543}" // โชว์เป็น พ.ศ.
                        }
                        showStartDatePicker = false
                    }) {
                        Text("ตกลง", color = LightPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showStartDatePicker = false }) {
                        Text("ยกเลิก", color = Color.Gray)
                    }
                }
            ) {
                DatePicker(
                    state = startDatePickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = LightPrimary,
                        todayDateBorderColor = LightPrimary,
                        todayContentColor = LightPrimary
                    )
                )
            }
        }

        // =====================================
        // 📅 DatePicker วันที่สิ้นสุด
        // =====================================
        if (showEndDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showEndDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        endDatePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.fromEpochMilliseconds(millis)
                            val localDate = instant.toLocalDateTime(TimeZone.UTC)

                            val day = localDate.dayOfMonth.toString().padStart(2, '0')
                            val month = localDate.monthNumber.toString().padStart(2, '0')
                            val engYear = localDate.year.toString()

                            apiEndedAt = "$engYear-$month-$day" // สำหรับส่ง Backend
                            endedAt = "$day/$month/${localDate.year + 543}" // โชว์เป็น พ.ศ.
                        }
                        showEndDatePicker = false
                    }) {
                        Text("ตกลง", color = LightPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEndDatePicker = false }) {
                        Text("ยกเลิก", color = Color.Gray)
                    }
                }
            ) {
                DatePicker(
                    state = endDatePickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = LightPrimary,
                        todayDateBorderColor = LightPrimary,
                        todayContentColor = LightPrimary
                    )
                )
            }
        }
    }
}

// 🌟 สร้าง Component ย่อยในไฟล์นี้เพื่อใช้ทำกล่องที่มี % และ DatePicker (เพื่อไม่ให้กระทบไฟล์กลาง)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onClick: (() -> Unit)? = null
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = LightPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        Box {
            TextField(
                value = value,
                onValueChange = onValueChange,
                readOnly = readOnly,
                placeholder = { Text(placeholder, color = Color.LightGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LightSoftWhite,
                    unfocusedContainerColor = LightSoftWhite,
                    disabledContainerColor = LightSoftWhite,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = Color(0xFF3A2F2A),
                    unfocusedTextColor = Color(0xFF3A2F2A)
                ),
                keyboardOptions = keyboardOptions,
                singleLine = true,
                trailingIcon = trailingIcon
            )

            // 🌟 ถ้ากล่องนี้ต้องกดเพื่อเปิดปฏิทิน ให้เอา Box ใสๆ มาครอบดักคลิกไว้
            if (onClick != null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { onClick() }
                )
            }
        }
    }
}